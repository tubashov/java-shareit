package ru.practicum.server.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.dto.booking.*;
import ru.practicum.server.exception.BookingAccessException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking createBooking(Long userId, BookingRequestDto dto) {

        if (dto.getStart() == null || dto.getEnd() == null)
            throw new IllegalArgumentException("Start and end must not be null");

        if (!dto.getStart().isBefore(dto.getEnd()))
            throw new IllegalArgumentException("Start must be before end");

        if (dto.getStart().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Start must not be in the past");

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("User", userId));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> NotFoundException.of("Item", dto.getItemId()));

        if (!item.getAvailable())
            throw new IllegalArgumentException("Item is not available for booking");

        if (item.getOwner().getId().equals(userId))
            throw new IllegalArgumentException("Owner cannot book their own item");

        // проверка перекрывающихся одобренных
        List<Booking> overlapping = bookingRepository
                .findAllByItemIdAndStatusAndStartBeforeAndEndAfter(
                        item.getId(), BookingStatus.APPROVED,
                        dto.getEnd(), dto.getStart());

        if (!overlapping.isEmpty())
            throw new IllegalArgumentException("Item already booked at this time");

        Booking booking = BookingMapper.toBooking(dto, BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(Long bookingId, Long ownerId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> NotFoundException.of("Booking", bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId))
            throw new BookingAccessException("Only owner can approve booking");

        if (booking.getStatus() == BookingStatus.APPROVED)
            throw new IllegalArgumentException("Booking already approved");

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBooking(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> NotFoundException.of("Booking", bookingId));

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId))
            throw new NotFoundException("Booking not found");

        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByUser(Long userId, String stateStr) {

        userRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("User", userId));

        BookingState state = parseState(stateStr);
        List<Booking> bookings =
                bookingRepository.findAllByBookerIdOrderByStartDesc(userId);

        return filterAndMap(bookings, state);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, String stateStr) {

        userRepository.findById(ownerId)
                .orElseThrow(() -> NotFoundException.of("User", ownerId));

        BookingState state = parseState(stateStr);
        List<Booking> bookings =
                bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);

        return filterAndMap(bookings, state);
    }

    private BookingState parseState(String stateStr) {
        try {
            return BookingState.valueOf(stateStr.toUpperCase());
        } catch (Exception ex) {
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

    @Override
    @Transactional(readOnly = true)
    public Optional<BookingShortDto> findLastBookingForItem(Long itemId) {
        return Optional.ofNullable(
                bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                        itemId, BookingStatus.APPROVED, LocalDateTime.now()
                )
        ).map(BookingMapper::toShortDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookingShortDto> findNextBookingForItem(Long itemId) {
        return Optional.ofNullable(
                bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                        itemId, BookingStatus.APPROVED, LocalDateTime.now()
                )
        ).map(BookingMapper::toShortDto);
    }
}
