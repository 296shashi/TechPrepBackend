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

    private String interviewer;

    private String date;      // You may also use LocalDate
    private String time;      // Or LocalTime

    private String type;
    private String status;

}
