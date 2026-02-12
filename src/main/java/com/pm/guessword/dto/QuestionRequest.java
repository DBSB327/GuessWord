package com.pm.guessword.dto;

import lombok.Data;

@Data
public class QuestionRequest {
    private String questionText;
    private String answer;
}
