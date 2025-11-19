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
    private Integer enrolled;
    private Double rating;
    private Double price;
    private String image;

    private List<SyllabusSectionDTO> syllabus;
}
