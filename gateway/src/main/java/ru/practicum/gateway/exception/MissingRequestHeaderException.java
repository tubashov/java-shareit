package ru.practicum.gateway.exception;

public class MissingRequestHeaderException extends RuntimeException {
    public MissingRequestHeaderException(String message) {
        super(message);
    }
}