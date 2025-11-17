package com.example.techprep.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String instructor;
    private String duration;
    private String level;
    private String category;

    private Integer lessons;
    private Integer enrolled;
    private Double rating;
    private Double price;
    private String image;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SyllabusSection> syllabus;
}

