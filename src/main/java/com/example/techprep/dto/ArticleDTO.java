package com.example.techprep.dto;

import lombok.Data;

@Data
public class ArticleDTO {
    private Long id;
    private String title;
    private String author;
    private String readTime;
    private String category;
}
