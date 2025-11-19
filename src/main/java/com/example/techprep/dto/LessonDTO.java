package com.example.techprep.dto;

import lombok.Data;

@Data
public class LessonDTO {

    private Long id;
    private String title;
    private String duration;
    private Boolean completed;
}
