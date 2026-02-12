package com.pm.guessword.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionResponse {
    private Long id;
    private String questionText;
    private LocalDateTime createdAt;
}
