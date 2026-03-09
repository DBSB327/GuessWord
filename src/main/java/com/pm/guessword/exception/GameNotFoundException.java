package com.pm.guessword.exception;

import jakarta.persistence.EntityNotFoundException;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long gameId) {
        super("Game with id " + gameId + " not found");
    }
}
