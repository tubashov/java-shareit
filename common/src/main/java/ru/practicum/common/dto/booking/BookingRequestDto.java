package ru.practicum.common.dto.booking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;

    public BookingRequestDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        this.itemId = itemId;
        this.start = start;
        this.end = end;
    }

    public BookingRequestDto() {
    }
}
