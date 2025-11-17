package com.example.techprep.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "interviewers")
@Data
public class Interviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String company;
    private String expertise;

    private Double rating;
    private Integer interviews;
    private Double price;
}
