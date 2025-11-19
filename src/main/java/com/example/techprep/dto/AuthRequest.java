package com.example.techprep.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
