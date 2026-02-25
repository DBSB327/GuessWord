package com.pm.guessword.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuestionRequest {
    @NotBlank(message = "Question text is required")
    @Size(min = 3, max = 300, message = "Question must be between 3 and 300 characters")
    private String questionText;
    @NotBlank(message = "Answer is required")
    private String answer;
}
