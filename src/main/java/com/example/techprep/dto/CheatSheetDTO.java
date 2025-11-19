package com.example.techprep.dto;

import lombok.Data;

@Data
public class CheatSheetDTO {
    private Long id;
    private String title;
    private String category;
    private Integer downloads;
}
