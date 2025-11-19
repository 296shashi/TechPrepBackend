package com.example.techprep.service;

import com.example.techprep.dto.InterviewDTO;
import com.example.techprep.entity.Interview;
import com.example.techprep.mapper.InterviewMapper;
import com.example.techprep.repository.InterviewerRepository;
import com.example.techprep.repository.InterviewRepository;
import com.example.techprep.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewMapper interviewMapper;
    private final UserRepository userRepository;
    private final InterviewerRepository interviewerRepository;
    private final com.example.techprep.service.GoogleService googleService;
    private final EmailService emailService;

    public InterviewService(InterviewRepository interviewRepository, InterviewMapper interviewMapper, UserRepository userRepository, InterviewerRepository interviewerRepository, com.example.techprep.service.GoogleService googleService, EmailService emailService) {
        this.interviewRepository = interviewRepository;
        this.interviewMapper = interviewMapper;
        this.userRepository = userRepository;
        this.interviewerRepository = interviewerRepository;
        this.googleService = googleService;
        this.emailService = emailService;
    }

    // Create
    public InterviewDTO createInterview(InterviewDTO interviewDTO) {
        Interview interview = interviewMapper.convertDTOToEntity(interviewDTO);
        // If interviewerId provided, fetch and set the interviewer and price
        if (interviewDTO.getInterviewerId() != null) {
            com.example.techprep.entity.Interviewer interviewer = interviewerRepository.findById(interviewDTO.getInterviewerId())
                    .orElseThrow(() -> new RuntimeException("Interviewer not found with id " + interviewDTO.getInterviewerId()));
            interview.setInterviewer(interviewer);
            if (interviewer.getPrice() != null) interview.setPrice(interviewer.getPrice());
        }
        Interview saved = interviewRepository.save(interview);
        return interviewMapper.convertEntityToDTO(saved);
    }

    // Create interview and associate with user by email
    public InterviewDTO createInterview(InterviewDTO interviewDTO, String userEmail) {
        Interview interview = interviewMapper.convertDTOToEntity(interviewDTO);
        if (userEmail != null) {
            com.example.techprep.entity.User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found with email " + userEmail));
            interview.setUser(user);
            // Set candidateName from user entity if not provided
            if (user.getName() != null && (interviewDTO.getCandidateName() == null || interviewDTO.getCandidateName().isEmpty())) {
                interview.setCandidateName(user.getName());
            } else if (interviewDTO.getCandidateName() != null) {
                interview.setCandidateName(interviewDTO.getCandidateName());
            }
        } else if (interviewDTO.getCandidateName() != null) {
            interview.setCandidateName(interviewDTO.getCandidateName());
        }
        // If interviewerId provided, set interviewer, interviewerName, and price
        if (interviewDTO.getInterviewerId() != null) {
            com.example.techprep.entity.Interviewer interviewer = interviewerRepository.findById(interviewDTO.getInterviewerId())
                    .orElseThrow(() -> new RuntimeException("Interviewer not found with id " + interviewDTO.getInterviewerId()));
            interview.setInterviewer(interviewer);
            if (interviewer.getPrice() != null) interview.setPrice(interviewer.getPrice());
            if (interviewer.getName() != null) interview.setInterviewerName(interviewer.getName());
        } else if (interviewDTO.getInterviewerName() != null) {
            interview.setInterviewerName(interviewDTO.getInterviewerName());
        }

        Interview saved = interviewRepository.save(interview);

        // Attempt to create a Meet using the admin account if available (admin will be invited along with participants)
        try {
            String meet = googleService.createGoogleMeetUsingAdminAccount(saved);
            if (meet != null) {
                saved.setMeetLink(meet);
                saved = interviewRepository.save(saved);
            }
        } catch (Exception e) {
            System.err.println("Failed to create Google Meet using admin: " + e.getMessage());
        }

        // Send email invite to candidate and interviewer if emails are present
        try {
            String[] recipients = new String[] {
                saved.getCandidateEmail(),
                saved.getInterviewerEmail()
            };
            String subject = "Your TechPrep Interview is Scheduled";
            StringBuilder body = new StringBuilder();
            body.append("Dear Participant,\n\n");
            body.append("Your interview is scheduled.\n");
            if (saved.getDate() != null && saved.getTime() != null) {
                body.append("Date: ").append(saved.getDate()).append("\n");
                body.append("Time: ").append(saved.getTime()).append("\n");
            }
            if (saved.getMeetLink() != null) {
                body.append("Google Meet Link: ").append(saved.getMeetLink()).append("\n");
            }
            body.append("\nBest of luck!\nTechPrep Team");
            // Only send to non-null, non-empty emails
            java.util.List<String> validRecipients = new java.util.ArrayList<>();
            for (String r : recipients) if (r != null && !r.isBlank()) validRecipients.add(r);
            if (!validRecipients.isEmpty()) {
                emailService.sendInterviewInvite(validRecipients.toArray(new String[0]), subject, body.toString());
            }
        } catch (Exception e) {
            System.err.println("Failed to send interview invite email: " + e.getMessage());
        }

        return interviewMapper.convertEntityToDTO(saved);
    }

    // Read all
    public List<InterviewDTO> getAllInterviews() {
        List<Interview> all = interviewRepository.findAll();
        return interviewMapper.convertEntityListToDTOList(all);
    }

    // Read all by user id
    public List<InterviewDTO> getAllInterviewsByUserId(Long userId) {
        List<Interview> all = interviewRepository.findAllByUser_Id(userId);
        return interviewMapper.convertEntityListToDTOList(all);
    }

    public List<InterviewDTO> getAllInterviewsByUserEmail(String userEmail) {
        if (userEmail == null) return getAllInterviews();
        com.example.techprep.entity.User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email " + userEmail));
        return getAllInterviewsByUserId(user.getId());
    }

    

    // Read by ID
    public Optional<InterviewDTO> getInterviewById(Long id) {
        return interviewRepository.findById(id).map(interviewMapper::convertEntityToDTO);
    }

    // Update
    public InterviewDTO updateInterview(Long id, InterviewDTO updatedInterview) {
        Interview existing = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found with id " + id));

        Interview toSave = interviewMapper.convertDTOToEntity(updatedInterview);
        toSave.setId(existing.getId());

        // Preserve or update interviewer/price if provided
        if (updatedInterview.getInterviewerId() != null) {
            com.example.techprep.entity.Interviewer interviewer = interviewerRepository.findById(updatedInterview.getInterviewerId())
                    .orElseThrow(() -> new RuntimeException("Interviewer not found with id " + updatedInterview.getInterviewerId()));
            toSave.setInterviewer(interviewer);
            if (interviewer.getPrice() != null) toSave.setPrice(interviewer.getPrice());
        } else {
            toSave.setInterviewer(existing.getInterviewer());
            toSave.setPrice(existing.getPrice());
        }

        Interview saved = interviewRepository.save(toSave);
        return interviewMapper.convertEntityToDTO(saved);
    }

    // Delete
    public void deleteInterview(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found with id " + id));
        interviewRepository.delete(interview);
    }

    // Create Meet for an existing interview using admin account
    public InterviewDTO createMeetForExistingInterviewUsingAdmin(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found with id " + id));
        try {
            String meet = googleService.createGoogleMeetUsingAdminAccount(interview);
            if (meet != null) {
                interview.setMeetLink(meet);
                interview = interviewRepository.save(interview);
                return interviewMapper.convertEntityToDTO(interview);
            }
        } catch (Exception e) {
            System.err.println("Failed to create Google Meet for interview: " + e.getMessage());
        }
        return null;
    }
}