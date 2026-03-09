package com.pm.guessword.exception;

import jakarta.persistence.EntityNotFoundException;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(Long questionId) {
        super("Question with id " + questionId + " not found");
    }
}
