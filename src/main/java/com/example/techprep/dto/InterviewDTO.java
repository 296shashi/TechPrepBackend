package com.example.techprep.dto;

import lombok.Data;

@Data
public class InterviewDTO {

    private Long id;

    private Long interviewerId;
    private String interviewer;
    private String candidateName;
    private String interviewerName;
    private String date;
    private String time;
    // ISO 8601 datetimes
    private String startDateTime;
    private String endDateTime;
    // participant emails
    private String interviewerEmail;
    private String candidateEmail;
    private String type;
    private String status;

    private Double price;

    // Google Meet link
    private String meetLink;

}
