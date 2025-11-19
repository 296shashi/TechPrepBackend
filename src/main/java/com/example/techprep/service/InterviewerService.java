package com.example.techprep.service;

import com.example.techprep.dto.InterviewerDTO;
import com.example.techprep.entity.Interviewer;
import com.example.techprep.mapper.InterviewerMapper;
import com.example.techprep.repository.InterviewerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InterviewerService {

    private final InterviewerRepository interviewerRepository;
    private final InterviewerMapper interviewerMapper;

    public InterviewerService(InterviewerRepository interviewerRepository, InterviewerMapper interviewerMapper) {
        this.interviewerRepository = interviewerRepository;
        this.interviewerMapper = interviewerMapper;
    }

    // Create
    public InterviewerDTO createInterviewer(InterviewerDTO interviewerDTO) {
        Interviewer interviewer = interviewerMapper.convertDTOToEntity(interviewerDTO);
        Interviewer saved = interviewerRepository.save(interviewer);
        return interviewerMapper.convertEntityToDTO(saved);
    }

    // Read all
    public List<InterviewerDTO> getAllInterviewers() {
        List<Interviewer> all = interviewerRepository.findAll();
        return interviewerMapper.convertEntityListToDTOList(all);
    }

    // Read by ID
    public Optional<InterviewerDTO> getInterviewerById(Long id) {
        return interviewerRepository.findById(id).map(interviewerMapper::convertEntityToDTO);
    }

    // Update
    public InterviewerDTO updateInterviewer(Long id, InterviewerDTO updatedInterviewer) {
        Interviewer existing = interviewerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interviewer not found with id " + id));

        Interviewer toSave = interviewerMapper.convertDTOToEntity(updatedInterviewer);
        toSave.setId(existing.getId());

        Interviewer saved = interviewerRepository.save(toSave);
        return interviewerMapper.convertEntityToDTO(saved);
    }

    // Delete
    public void deleteInterviewer(Long id) {
        Interviewer interviewer = interviewerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interviewer not found with id " + id));
        interviewerRepository.delete(interviewer);
    }
}
