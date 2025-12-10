package ru.practicum.common.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    @Positive(message = "Item ID must be positive")
    private Long itemId;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "End date cannot be null")
    @Future
    private LocalDateTime end;
}
