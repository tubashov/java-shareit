package ru.practicum.server.booking;

import ru.practicum.common.dto.booking.BookingRequestDto;
import ru.practicum.common.dto.booking.BookingResponseDto;
import ru.practicum.common.dto.booking.BookingShortDto;
import ru.practicum.common.dto.booking.BookingStatus;

public class BookingMapper {

    public static BookingResponseDto toResponseDto(Booking booking) {
        if (booking == null) return null;

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(BookingResponseDto.BookerDto.builder()
                        .id(booking.getBooker() != null ? booking.getBooker().getId() : null)
                        .build())
                .item(BookingResponseDto.ItemInBookingDto.builder()
                        .id(booking.getItem() != null ? booking.getItem().getId() : null)
                        .name(booking.getItem() != null ? booking.getItem().getName() : null)
                        .build())
                .build();
    }

    public static BookingShortDto toShortDto(Booking booking) {
        if (booking == null) return null;
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public static Booking toBooking(BookingRequestDto req, BookingStatus status) {
        if (req == null) return null;
        return Booking.builder()
                .start(req.getStart())
                .end(req.getEnd())
                .status(status)
                .build();
    }
}
