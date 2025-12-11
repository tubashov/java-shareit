package ru.practicum.server.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.common.dto.booking.*;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;
import ru.practicum.server.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Item item;
    private BookingRequestDto requestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");

        item = new Item();
        item.setId(100L);
        item.setOwner(user);
        item.setAvailable(true);

        requestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void createBooking_success() {
        User owner = new User(1L, "owner", "owner@mail.com");
        User booker = new User(2L, "user", "u@mail.com");

        item.setOwner(owner);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findAllByItemIdAndStatusAndStartBeforeAndEndAfter(
                anyLong(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        Booking savedBooking = new Booking();
        savedBooking.setId(10L);
        savedBooking.setItem(item);
        savedBooking.setBooker(booker);
        savedBooking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);

        Booking result = bookingService.createBooking(booker.getId(), requestDto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(booker.getId(), result.getBooker().getId());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_itemNotAvailable_throws() {
        item.setAvailable(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(user.getId(), requestDto));
        assertEquals("Item is not available for booking", ex.getMessage());
    }

    @Test
    void createBooking_userIsOwner_throws() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(user.getId(), requestDto));
        assertEquals("Owner cannot book their own item", ex.getMessage());
    }

    @Test
    void getBooking_success() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBooking(1L, user.getId());
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getBooking_notOwnerOrBooker_throws() {
        Booking booking = new Booking();
        booking.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);
        Item item = new Item();
        item.setOwner(otherUser);
        booking.setItem(item);

        User booker = new User();
        booker.setId(3L);
        booking.setBooker(booker);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(1L, user.getId()));
    }

    @Test
    void findLastBookingForItem_returnsBookingShortDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                eq(item.getId()), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(booking);

        Optional<BookingShortDto> result = bookingService.findLastBookingForItem(item.getId());

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
    }

    @Test
    void findNextBookingForItem_returnsBookingShortDto() {
        Booking booking = new Booking();
        booking.setId(2L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                eq(item.getId()), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(booking);

        Optional<BookingShortDto> result = bookingService.findNextBookingForItem(item.getId());

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
    }
}
