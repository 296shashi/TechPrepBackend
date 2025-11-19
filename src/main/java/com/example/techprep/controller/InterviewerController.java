package com.example.techprep.controller;

import com.example.techprep.dto.InterviewerDTO;
import com.example.techprep.service.InterviewerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviewers")
public class InterviewerController {

    private final InterviewerService interviewerService;

    public InterviewerController(InterviewerService interviewerService) {
        this.interviewerService = interviewerService;
    }

    // Create
    @PostMapping
    public ResponseEntity<InterviewerDTO> createInterviewer(@RequestBody InterviewerDTO interviewer) {
        return ResponseEntity.ok(interviewerService.createInterviewer(interviewer));
    }

    // Read all
    @GetMapping
    public ResponseEntity<List<InterviewerDTO>> getAllInterviewers() {
        return ResponseEntity.ok(interviewerService.getAllInterviewers());
    }

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<InterviewerDTO> getInterviewerById(@PathVariable Long id) {
        return interviewerService.getInterviewerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<InterviewerDTO> updateInterviewer(@PathVariable Long id, @RequestBody InterviewerDTO interviewer) {
        return ResponseEntity.ok(interviewerService.updateInterviewer(id, interviewer));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterviewer(@PathVariable Long id) {
        interviewerService.deleteInterviewer(id);
        return ResponseEntity.noContent().build();
    }
}
