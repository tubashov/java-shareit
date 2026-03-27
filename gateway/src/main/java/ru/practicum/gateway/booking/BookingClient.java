package ru.practicum.gateway.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.common.dto.booking.BookItemRequestDto;
import ru.practicum.common.dto.booking.BookingState;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.client.ClientRestFactory;

import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(ClientRestFactory.build(serverUrl + API_PREFIX, builder));
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        log.info("BookingClient: getBookings called for userId={}, state={}, from={}, size={}", userId, state, from, size);
        return get("?state={state}&from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        log.info("BookingClient: bookItem called for userId={}, request={}", userId, requestDto);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        log.info("BookingClient: getBooking called for userId={}, bookingId={}", userId, bookingId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> approveBooking(long userId, Long bookingId, boolean approved) {
        log.info("BookingClient: approveBooking called for userId={}, bookingId={}, approved={}", userId, bookingId, approved);
        Map<String, Object> params = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, params, null);
    }

    public ResponseEntity<Object> getBookingsForOwner(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        log.info("BookingClient: getBookingsForOwner userId={}, state={}, from={}, size={}", userId, state, from, size);
        return get("/owner?state={state}&from={from}&size={size}", userId, params);
    }
}
