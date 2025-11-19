package com.example.techprep.dto;

import lombok.Data;

@Data
public class InterviewerDTO {

    private Long id;
    private String name;
    private String company;
    private String expertise;
    private Double rating;
    private Integer interviews;
    private Double price;

}
