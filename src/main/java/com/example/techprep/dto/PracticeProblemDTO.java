package com.example.techprep.dto;

import lombok.Data;

@Data
public class PracticeProblemDTO {
    private Long id;
    private String title;
    private String difficulty;
    private String category;
    private Boolean solved;
}
