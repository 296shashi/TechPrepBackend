package com.example.techprep.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseDTO {

    private Long id;

    private String title;
    private String description;
    private String instructor;
    private String duration;
    private String level;
    private String category;

    private Integer lessons;
    private Integer enrolledCount;
    private Double rating;
    private Double price;
    private String image;

    private List<SyllabusSectionDTO> syllabus;

    // Indicates if the current user is enrolled in this course
    private boolean enrolled;
    // Total lessons in the course (computed from syllabus)
    private Integer totalLessons;
    // Number of lessons completed by the current user
    private Integer completedLessons;
    // List of lesson ids completed by the current user (for this course)
    private java.util.List<Long> completedLessonIds;
}
