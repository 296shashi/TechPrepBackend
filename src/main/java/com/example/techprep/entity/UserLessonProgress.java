package com.example.techprep.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "user_lesson_progress", indexes = {@Index(columnList = "user_id, course_id", name = "idx_ulp_user_course")})
@Data
public class UserLessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // The lesson id within the course (from syllabus)
    private Long lessonId;

    // The syllabus id (optional) to relate progress back to a specific syllabus entry
    private Long syllabusId;

    // Whether the user has completed this lesson (nullable)
    @Column(name = "completed")
    private Boolean completed;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

}
