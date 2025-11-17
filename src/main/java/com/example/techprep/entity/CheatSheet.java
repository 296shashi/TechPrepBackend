package com.example.techprep.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cheat_sheets")
@Data
public class CheatSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;
    private Integer downloads;
}
