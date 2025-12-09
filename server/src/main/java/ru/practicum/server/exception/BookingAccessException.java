package ru.practicum.server.exception;

public class BookingAccessException extends RuntimeException {
    public BookingAccessException(String message) {
        super(message);
    }
}
