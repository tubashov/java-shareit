package ru.practicum.server.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.booking.BookingRequestDto;
import ru.practicum.common.dto.booking.BookingResponseDto;

import java.util.List;

/**
 * Контроллер Booking в модуле server.
 * Контроллер прост: он принимает HTTP-запросы, вызывает сервис и возвращает DTO.
 * Бизнес-валидация (даты, права, проверки пересечений и т.п.) выполняется в BookingService.
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingRequestDto requestDto
    ) {
        return BookingMapper.toResponseDto(bookingService.createBooking(userId, requestDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam boolean approved
    ) {
        return BookingMapper.toResponseDto(bookingService.approveBooking(bookingId, ownerId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return BookingMapper.toResponseDto(bookingService.getBooking(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return bookingService.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return bookingService.getBookingsByOwner(ownerId, state);
    }
}
