package ru.practicum.common.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;

    private BookerDto booker;

    private ItemInBookingDto item;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookerDto {
        private Long id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemInBookingDto {
        private Long id;

        private String name;
    }
}
