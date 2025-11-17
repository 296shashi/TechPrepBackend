package com.example.techprep.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
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
    private SyllabusSection section;

}