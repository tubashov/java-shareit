package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking createBooking(Long userId, BookingRequestDto requestDto);

    Booking approveBooking(Long bookingId, Long ownerId, boolean approved);

    Booking getBooking(Long bookingId, Long userId);

    List<BookingResponseDto> getBookingsByUser(Long userId, String state);

    List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state);

    Optional<BookingShortDto> findLastBookingForItem(Long itemId);

    Optional<BookingShortDto> findNextBookingForItem(Long itemId);
}
