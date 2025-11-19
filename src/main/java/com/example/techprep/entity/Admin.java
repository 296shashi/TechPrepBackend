package com.example.techprep.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "admins", indexes = {@Index(columnList = "email", name = "idx_admins_email")})
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Google OAuth tokens
    @Lob
    @Column(name = "google_access_token", columnDefinition = "TEXT")
    private String googleAccessToken;

    @Lob
    @Column(name = "google_refresh_token", columnDefinition = "TEXT")
    private String googleRefreshToken;

    private Long googleTokenExpiry;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = new java.util.Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new java.util.Date();
    }
}
