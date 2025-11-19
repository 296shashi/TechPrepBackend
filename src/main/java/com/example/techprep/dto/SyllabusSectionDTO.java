package com.example.techprep.dto;

import lombok.Data;

import java.util.List;

@Data
public class SyllabusSectionDTO {

    private Long id;
    private String title;

    private List<LessonDTO> lessons;
}
