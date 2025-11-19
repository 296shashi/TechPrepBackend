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
    private Boolean completed;

    @ManyToOne
    @JoinColumn(name = "section_id")
    @JsonBackReference(value = "section-lessons")
    private SyllabusSection section;

}