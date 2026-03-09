package com.pm.guessword.exception;

import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }

    public UserNotFoundException(String username) {
        super("User with name " + username + " not found");
    }
}
