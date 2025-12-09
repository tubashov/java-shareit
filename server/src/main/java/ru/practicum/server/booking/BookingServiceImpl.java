package ru.practicum.server.booking;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.dto.booking.BookingRequestDto;
import ru.practicum.common.dto.booking.BookingResponseDto;
import ru.practicum.common.dto.booking.BookingState;
import ru.practicum.common.dto.booking.BookingStatus;
import ru.practicum.common.dto.booking.BookingShortDto;
import ru.practicum.server.exception.BookingAccessException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking createBooking(Long userId, BookingRequestDto requestDto) {
        if (requestDto == null) throw new IllegalArgumentException("Booking data cannot be null");
        if (requestDto.getStart() == null || requestDto.getEnd() == null)
            throw new IllegalArgumentException("Start and end must be provided");
        if (!requestDto.getStart().isBefore(requestDto.getEnd()))
            throw new IllegalArgumentException("Start must be before end");

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("User", userId));

        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> NotFoundException.of("Item", requestDto.getItemId()));

        if (!item.getAvailable())
            throw new IllegalStateException("Item is not available for booking");

        if (Objects.equals(item.getOwner().getId(), userId))
            throw new IllegalStateException("Owner cannot book their own item");

        List<Booking> overlapping = bookingRepository.findAllByItemIdAndStatusAndStartBeforeAndEndAfter(
                item.getId(), BookingStatus.APPROVED, requestDto.getEnd(), requestDto.getStart());
        if (!overlapping.isEmpty())
            throw new IllegalStateException("Item already booked at this time");

        Booking booking = BookingMapper.toBooking(requestDto, BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created: id={}, userId={}, itemId={}", saved.getId(), userId, item.getId());
        return saved;
    }

    @Override
    public Booking approveBooking(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> NotFoundException.of("Booking", bookingId));

        Item item = booking.getItem();
        if (!Objects.equals(item.getOwner().getId(), ownerId))
            throw new BookingAccessException("Only item owner can approve/reject booking");

        if (!(booking.getStatus() == BookingStatus.APPROVED && approved)) {
            booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            bookingRepository.save(booking);
        }

        log.info("Booking status updated: id={}, status={}", booking.getId(), booking.getStatus());
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> NotFoundException.of("Booking", bookingId));

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!Objects.equals(ownerId, userId) && !Objects.equals(bookerId, userId))
            throw new NotFoundException("Access denied to booking " + bookingId);

        log.info("Booking retrieved: id={}, userId={}", booking.getId(), userId);
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByUser(Long userId, String stateStr) {
        userRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("User", userId));

        BookingState state = parseState(stateStr);

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        List<BookingResponseDto> result = filterAndMap(bookings, state);

        log.info("Bookings retrieved for user {}: count={}", userId, result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, String stateStr) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> NotFoundException.of("User", ownerId));

        BookingState state = parseState(stateStr);

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
        List<BookingResponseDto> result = filterAndMap(bookings, state);

        log.info("Bookings retrieved for owner {}: count={}", ownerId, result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public Optional<BookingShortDto> findLastBookingForItem(Long itemId) {
        Optional<BookingShortDto> result = Optional.ofNullable(
                        bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                                itemId, BookingStatus.APPROVED, LocalDateTime.now()))
                .map(BookingMapper::toShortDto);

        result.ifPresent(b -> log.info("Last booking for item {}: id={}", itemId, b.getId()));
        return result;
    }

    @Transactional(readOnly = true)
    public Optional<BookingShortDto> findNextBookingForItem(Long itemId) {
        Optional<BookingShortDto> result = Optional.ofNullable(
                        bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                                itemId, BookingStatus.APPROVED, LocalDateTime.now()))
                .map(BookingMapper::toShortDto);

        result.ifPresent(b -> log.info("Next booking for item {}: id={}", itemId, b.getId()));
        return result;
    }

    // --- helpers ---
    private BookingState parseState(String stateStr) {
        if (stateStr == null) return BookingState.ALL;
        try {
            return BookingState.valueOf(stateStr.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown state: " + stateStr);
        }
    }

    private List<BookingResponseDto> filterAndMap(List<Booking> bookings, BookingState state) {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream()
                .filter(b -> switch (state) {
                    case ALL -> true;
                    case CURRENT -> !b.getStart().isAfter(now) && !b.getEnd().isBefore(now);
                    case PAST -> b.getEnd().isBefore(now);
                    case FUTURE -> b.getStart().isAfter(now);
                    case WAITING -> b.getStatus() == BookingStatus.WAITING;
                    case REJECTED -> b.getStatus() == BookingStatus.REJECTED;
                    case APPROVED -> b.getStatus() == BookingStatus.APPROVED;
                })
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
