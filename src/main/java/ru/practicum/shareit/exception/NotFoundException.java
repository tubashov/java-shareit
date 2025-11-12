package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException of(String entity, Object id) {
        if (id == null) {
            return new NotFoundException(entity + " not found");
        }
        return new NotFoundException(entity + " with id=" + id + " not found");
    }
}
