package com.example.techprep.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "external_links")
@Data
public class ExternalLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String url;
    private String category;
}
