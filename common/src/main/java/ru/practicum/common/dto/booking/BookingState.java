package ru.practicum.common.dto.booking;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    APPROVED;

    public static Optional<BookingState> from(String state) {
        try {
            return Optional.of(BookingState.valueOf(state.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
