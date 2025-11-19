package com.example.techprep.dto;

import lombok.Data;

@Data
public class ExternalLinkDTO {
    private Long id;
    private String title;
    private String description;
    private String url;
    private String category;
}
