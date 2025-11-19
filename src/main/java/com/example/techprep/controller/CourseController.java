package com.example.techprep.controller;

import com.example.techprep.dto.CourseDTO;
import com.example.techprep.entity.SyllabusSection;
import com.example.techprep.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Create
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        // if authenticated, enroll creator automatically
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth != null ? String.valueOf(auth.getPrincipal()) : null;
        CourseDTO createdCourse = courseService.createCourse(courseDTO, email);
        return ResponseEntity.ok(createdCourse);
    }

    // Read all (optionally for authenticated user)
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses(Authentication authentication) {
        String email = authentication != null ? String.valueOf(authentication.getPrincipal()) : null;
        List<CourseDTO> courses = courseService.getAllCourses(email);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/enrolled")
    public ResponseEntity<List<CourseDTO>> getEnrolledCourses(Authentication authentication) {
        String email = null;
        try {
            if (authentication != null) {
                email = authentication.getName();
            }
        } catch (Exception e) {
            logger.warn("Failed to extract authentication name: {}", e.getMessage());
            email = null;
        }

        List<CourseDTO> allCourses = courseService.getAllCourses(email);
        // Only return those where enrolled == true
        List<CourseDTO> enrolledCourses = new java.util.ArrayList<>();
        for (CourseDTO dto : allCourses) {
            if (dto.isEnrolled()) {
                enrolledCourses.add(dto);
            }
        }
        return ResponseEntity.ok(enrolledCourses);
    }

   // Read by ID
   @GetMapping("/{id}")
   public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id, Authentication authentication) {
       String email = authentication != null ? String.valueOf(authentication.getPrincipal()) : null;
       CourseDTO dto = courseService.getCourseById(id, email)
               .orElseThrow(() -> new RuntimeException("CourseDTO not found with id " + id));
       return ResponseEntity.ok(dto);
   }

   // Update
   @PutMapping("/{id}")
   public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDetails) {
       CourseDTO updatedCourse = courseService.updateCourse(id, courseDetails);
       return ResponseEntity.ok(updatedCourse);
   }

   // Delete
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
       courseService.deleteCourse(id);
       return ResponseEntity.noContent().build();
   }

    // Enroll current authenticated user
    @PostMapping("/{id}/enroll")
    public ResponseEntity<Void> enrollUser(@PathVariable("id") Long courseId, Authentication authentication) {
        String email = authentication != null ? String.valueOf(authentication.getPrincipal()) : null;
        courseService.enrollUser(courseId, email);
        return ResponseEntity.ok().build();
    }

    // Unenroll current authenticated user
    @PostMapping("/{id}/unenroll")
    public ResponseEntity<Void> unenrollUser(@PathVariable("id") Long courseId, Authentication authentication) {
        String email = authentication != null ? String.valueOf(authentication.getPrincipal()) : null;
        courseService.unenrollUser(courseId, email);
        return ResponseEntity.ok().build();
    }

   // Get syllabus by course ID
   @GetMapping("/syllabus/{courseId}")
   public ResponseEntity<List<SyllabusSection>> getSyllabusByCourseId(@PathVariable Long courseId) {
       List<SyllabusSection> syllabus = courseService.getSyllabusByCourseId(courseId);
       return ResponseEntity.ok(syllabus);
   }
}
