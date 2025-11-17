package com.example.techprep.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "articles")
@Data
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String readTime;
    private String category;
}
