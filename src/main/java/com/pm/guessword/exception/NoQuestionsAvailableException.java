package com.pm.guessword.exception;

public class NoQuestionsAvailableException extends RuntimeException {
    public NoQuestionsAvailableException() {
        super("No available questions found");
    }
}
