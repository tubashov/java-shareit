package ru.practicum.common.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;

    private Long itemId;

    private Long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}
