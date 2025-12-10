package ru.practicum.common.dto.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookItemRequestDto {
    @NotNull(message = "Item ID cannot be null")
    @Positive(message = "Item ID must be positive")
    private Long itemId;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "End date cannot be null")
    @Future
    private LocalDateTime end;
}
