package ru.practicum.gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.booking.BookItemRequestDto;
import ru.practicum.common.dto.booking.BookingState;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Map;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Get booking request with state={}, userId={}, from={}, size={}", stateParam, userId, from, size);

        // Проверка userId
        if (userId <= 0) {
            log.warn("Invalid userId: {}", userId);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "X-Sharer-User-Id must be positive",
                    "status", 400
            ));
        }

        // Преобразуем stateParam в BookingState
        BookingState state;
        try {
            state = BookingState.from(stateParam)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid state parameter: {}", stateParam);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", ex.getMessage(),
                    "status", 400
            ));
        }

        // Вызов сервиса через клиента
        try {
            ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);
            log.info("Get bookings response for userId={}, status={}", userId, response.getStatusCode());
            return response;
        } catch (Exception ex) {
            log.error("Error fetching bookings for userId={}: {}", userId, ex.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal server error",
                    "status", 500
            ));
        }
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        log.info("Approve booking {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwner(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Get bookings for owner, state={}, userId={}, from={}, size={}", stateParam, userId, from, size);

        if (userId <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "X-Sharer-User-Id must be positive"));
        }

        BookingState state;
        try {
            state = BookingState.from(stateParam)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }

        return bookingClient.getBookingsForOwner(userId, state, from, size);
    }
}
