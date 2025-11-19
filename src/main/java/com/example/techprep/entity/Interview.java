package com.example.techprep.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "interviewer_id")
    private com.example.techprep.entity.Interviewer interviewer;

    private String date;      // You may also use LocalDate
    private String time;      // Or LocalTime

    // precise start/end datetimes in ISO 8601 (e.g. 2025-11-18T10:00:00Z)
    private String startDateTime;
    private String endDateTime;
    // participant emails (optional)
    private String interviewerEmail;
    private String candidateEmail;

    private String type;
    private String status;
    private String candidateName;
    private String interviewerName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"enrolledCourses", "password"})
    private User user;

    private Double price;

    // Google Meet link
    private String meetLink;
}
