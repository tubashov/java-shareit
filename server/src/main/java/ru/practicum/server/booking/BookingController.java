package ru.practicum.server.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.booking.BookingRequestDto;
import ru.practicum.common.dto.booking.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                     @Valid @RequestBody BookingRequestDto requestDto) {
        return BookingMapper.toResponseDto(bookingService.createBooking(userId, requestDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@PathVariable @Positive Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
                                      @RequestParam boolean approved) {
        return BookingMapper.toResponseDto(bookingService.approveBooking(bookingId, ownerId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable @Positive Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return BookingMapper.toResponseDto(bookingService.getBooking(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsByOwner(ownerId, state);
    }
}
