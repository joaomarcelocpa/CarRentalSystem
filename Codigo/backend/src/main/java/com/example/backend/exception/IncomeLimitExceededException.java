package com.example.backend.exception;

public class IncomeLimitExceededException extends RuntimeException {
    public IncomeLimitExceededException(String message) {
        super(message);
    }
}