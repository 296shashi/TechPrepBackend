package com.example.techprep.controller;

import com.example.techprep.dto.CourseDTO;
import com.example.techprep.service.CourseService;
import com.example.techprep.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final CourseService courseService;

    public ProgressController(ProgressService progressService, CourseService courseService) {
        this.progressService = progressService;
        this.courseService = courseService;
    }

    // GET completed lesson ids for authenticated user for a course
    @GetMapping("/course/{courseId}/lessons")
    public ResponseEntity<List<Long>> getCompletedLessons(@PathVariable Long courseId, Authentication authentication) {
        String email = null;
        if (authentication != null) {
            try { email = authentication.getName(); } catch (Exception ignored) { email = null; }
        }
        if (email == null) return ResponseEntity.status(401).build();
        List<Long> ids = progressService.getCompletedLessonIdsForUser(email, courseId);
        return ResponseEntity.ok(ids);
    }

    // POST to set a lesson completed/uncompleted and return updated CourseDTO
    @PostMapping("/course/{courseId}/lessons")
    public ResponseEntity<?> setLessonCompletion(@PathVariable Long courseId, @RequestBody Map<String, Object> body, Authentication authentication) {
        String email = null;
        if (authentication != null) {
            try { email = authentication.getName(); } catch (Exception ignored) { email = null; }
        }
        if (email == null) return ResponseEntity.status(401).build();
        Long lessonId = ((Number) body.get("lessonId")).longValue();
        boolean completed = Boolean.TRUE.equals(body.get("completed"));
        Long syllabusId = null;
        if (body.get("syllabusId") != null) {
            try {
                syllabusId = ((Number) body.get("syllabusId")).longValue();
            } catch (Exception ignored) { }
        }
        progressService.setLessonCompletion(email, courseId, lessonId, completed, syllabusId);

        // Return updated CourseDTO for the authenticated user to avoid extra roundtrip
        try {
            var optional = courseService.getCourseById(courseId, email);
            if (optional.isPresent()) {
                CourseDTO dto = optional.get();
                return ResponseEntity.ok(dto);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.status(404).build();
    }
}
