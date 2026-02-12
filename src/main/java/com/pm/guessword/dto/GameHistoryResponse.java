package com.pm.guessword.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameHistoryResponse {
    private Long id;
    private String questionText;
    private String guessedWord;
    private boolean correct;
    private int attempts;
    private LocalDateTime date;
}
