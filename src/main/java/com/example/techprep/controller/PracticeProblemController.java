package com.example.techprep.controller;

import com.example.techprep.dto.PracticeProblemDTO;
import com.example.techprep.service.PracticeProblemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practice-problems")
public class PracticeProblemController {
    private final PracticeProblemService service;

    public PracticeProblemController(PracticeProblemService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PracticeProblemDTO>> getAllPracticeProblems() {
        return ResponseEntity.ok(service.getAllPracticeProblems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PracticeProblemDTO> getPracticeProblemById(@PathVariable Long id) {
        return service.getPracticeProblemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PracticeProblemDTO> createPracticeProblem(@RequestBody PracticeProblemDTO dto) {
        return ResponseEntity.ok(service.createPracticeProblem(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PracticeProblemDTO> updatePracticeProblem(@PathVariable Long id, @RequestBody PracticeProblemDTO dto) {
        return ResponseEntity.ok(service.updatePracticeProblem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePracticeProblem(@PathVariable Long id) {
        service.deletePracticeProblem(id);
        return ResponseEntity.noContent().build();
    }
}
