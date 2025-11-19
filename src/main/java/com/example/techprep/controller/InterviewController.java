package com.example.techprep.controller;

import com.example.techprep.dto.InterviewDTO;
import com.example.techprep.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    // Create (associate with authenticated user if present)
    @PostMapping
    public ResponseEntity<InterviewDTO> createInterview(@RequestBody InterviewDTO interview, Authentication authentication) {
        String email = authentication != null ? String.valueOf(authentication.getPrincipal()) : null;
        return ResponseEntity.ok(interviewService.createInterview(interview, email));
    }

    // Read all (for authenticated user)
    @GetMapping
    public ResponseEntity<List<InterviewDTO>> getAllInterviews(Authentication authentication) {
        String email = authentication != null ? String.valueOf(authentication.getPrincipal()) : null;
        if (email != null) {
            return ResponseEntity.ok(interviewService.getAllInterviewsByUserEmail(email));
        }
        return ResponseEntity.ok(interviewService.getAllInterviews());
    }

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<InterviewDTO> getInterviewById(@PathVariable Long id) {
        return interviewService.getInterviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<InterviewDTO> updateInterview(@PathVariable Long id, @RequestBody InterviewDTO interview) {
        return ResponseEntity.ok(interviewService.updateInterview(id, interview));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterview(@PathVariable Long id) {
        interviewService.deleteInterview(id);
        return ResponseEntity.noContent().build();
    }
    
    // Create Meet for an existing interview using admin account
    @PostMapping("/{id}/create-meet")
    public ResponseEntity<InterviewDTO> createMeetForInterview(@PathVariable Long id) {
        InterviewDTO dto = interviewService.createMeetForExistingInterviewUsingAdmin(id);
        if (dto == null) return ResponseEntity.status(500).build();
        return ResponseEntity.ok(dto);
    }
}
