package ru.practicum.common.dto.booking;

import jakarta.validation.constraints.*;
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

    @AssertTrue(message = "Start must be before end")
    public boolean isStartBeforeEnd() {
        return start != null && end != null && start.isBefore(end);
    }
}
