package com.pm.guessword.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionAdminResponse {
    private Long id;
    private String questionText;
    private String answer;
    private LocalDateTime createdAt;
    private String createdByUsername;
}
