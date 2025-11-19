package com.example.techprep.controller;

import com.example.techprep.dto.ExternalLinkDTO;
import com.example.techprep.service.ExternalLinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/external-links")
public class ExternalLinkController {
    private final ExternalLinkService service;

    public ExternalLinkController(ExternalLinkService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ExternalLinkDTO>> getAllExternalLinks() {
        return ResponseEntity.ok(service.getAllExternalLinks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExternalLinkDTO> getExternalLinkById(@PathVariable Long id) {
        return service.getExternalLinkById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExternalLinkDTO> createExternalLink(@RequestBody ExternalLinkDTO dto) {
        return ResponseEntity.ok(service.createExternalLink(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExternalLinkDTO> updateExternalLink(@PathVariable Long id, @RequestBody ExternalLinkDTO dto) {
        return ResponseEntity.ok(service.updateExternalLink(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExternalLink(@PathVariable Long id) {
        service.deleteExternalLink(id);
        return ResponseEntity.noContent().build();
    }
}
