package com.example.techprep.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonManagedReference
    private List<SyllabusSection> syllabus;

    @ManyToMany(mappedBy = "enrolledCourses")
    @JsonIgnoreProperties("enrolledCourses")
    private java.util.Set<com.example.techprep.entity.User> enrolledUsers = new java.util.HashSet<>();
}

