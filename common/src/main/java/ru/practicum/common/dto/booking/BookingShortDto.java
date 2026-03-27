package ru.practicum.common.dto.booking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingShortDto {
    private Long id;

    private Long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;
}
