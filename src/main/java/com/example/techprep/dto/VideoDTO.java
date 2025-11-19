package com.example.techprep.dto;

import lombok.Data;

@Data
public class VideoDTO {
    private Long id;
    private String title;
    private String duration;
    private String views;
    private String category;
}
