package com.example.techprep.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String duration;

    // Whether this lesson is active/available to users
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LessonStatus status = LessonStatus.INACTIVE;

    @ManyToOne
    @JoinColumn(name = "section_id")
    @JsonBackReference(value = "section-lessons")
    private SyllabusSection section;

}