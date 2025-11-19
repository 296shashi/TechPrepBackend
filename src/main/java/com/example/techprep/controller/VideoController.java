package com.example.techprep.controller;

import com.example.techprep.dto.VideoDTO;
import com.example.techprep.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    private final VideoService service;

    public VideoController(VideoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<VideoDTO>> getAllVideos() {
        return ResponseEntity.ok(service.getAllVideos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> getVideoById(@PathVariable Long id) {
        return service.getVideoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VideoDTO> createVideo(@RequestBody VideoDTO dto) {
        return ResponseEntity.ok(service.createVideo(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoDTO> updateVideo(@PathVariable Long id, @RequestBody VideoDTO dto) {
        return ResponseEntity.ok(service.updateVideo(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        service.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}
