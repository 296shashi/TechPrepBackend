package com.example.techprep.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {@Index(columnList = "email", name = "idx_users_email")})
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // stored as BCrypt hash

    // Roles stored as comma-separated values, e.g. "USER,ADMIN"
    private String roles = "USER";

    @ManyToMany
    @JoinTable(
        name = "user_courses",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @JsonIgnoreProperties("enrolledUsers")
    private Set<com.example.techprep.entity.Course> enrolledCourses = new HashSet<>();

    // Google OAuth tokens (nullable)
    private String googleAccessToken;
    private String googleRefreshToken;
    private Long googleTokenExpiry; // epoch millis
}
