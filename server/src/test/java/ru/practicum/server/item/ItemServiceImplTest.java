package ru.practicum.server.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.common.dto.item.CommentDto;
import ru.practicum.common.dto.item.ItemDto;
import ru.practicum.server.booking.BookingRepository;
import ru.practicum.server.comment.Comment;
import ru.practicum.server.comment.CommentRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.request.ItemRequestRepository;

import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private ItemDto itemDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        itemDto = ItemDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .build();

        itemRequest = ItemRequest.builder()
                .id(5L)
                .build();
    }

    @Test
    void createItem_success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenAnswer(i -> {
            var item = i.getArgument(0, Item.class);
            item.setId(10L);
            return item;
        });

        ItemDto result = itemService.createItem(itemDto, user.getId());

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(user.getId(), result.getOwnerId());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void createItem_withRequest_success() {
        itemDto.setRequestId(itemRequest.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenAnswer(i -> {
            var item = i.getArgument(0, Item.class);
            item.setId(11L);
            return item;
        });

        ItemDto result = itemService.createItem(itemDto, user.getId());

        assertNotNull(result);
        assertEquals(11L, result.getId());
        verify(itemRequestRepository).findById(itemRequest.getId());
    }

    @Test
    void updateItem_success() {
        var item = Item.builder()
                .id(10L)
                .owner(user)
                .available(true)
                .name("Old")
                .description("Old desc")
                .build();

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ItemDto updates = ItemDto.builder()
                .name("New")
                .description("New desc")
                .available(false)
                .build();

        var result = itemService.update(10L, updates, user.getId());

        assertEquals("New", result.getName());
        assertEquals("New desc", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void updateItem_notOwner_throws() {
        var item = Item.builder().id(10L).owner(User.builder().id(2L).build()).build();
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        Exception ex = assertThrows(RuntimeException.class,
                () -> itemService.update(10L, itemDto, user.getId()));
        assertTrue(ex.getMessage().contains("Item owner"));
    }

    @Test
    void getById_ownerView_withCommentsAndBookings() {
        var item = Item.builder().id(10L).owner(user).build();
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(10L)).thenReturn(List.of(
                Comment.builder().id(1L).text("Nice").author(user).item(item).build()
        ));
        when(bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(anyLong(), any(), any()))
                .thenReturn(null);

        ItemDto result = itemService.getById(10L, user.getId());

        assertNotNull(result);
        assertEquals(1, result.getComments().size());
    }

    @Test
    void search_returnsItems() {
        Item item = Item.builder().id(10L).name("Drill").description("Power drill").available(true).build();
        when(itemRepository.findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(anyString(), anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> results = itemService.search("drill");

        assertEquals(1, results.size());
        assertEquals(item.getName(), results.get(0).getName());
    }

    @Test
    void addComment_success() {
        Item item = Item.builder().id(10L).owner(user).build();
        CommentDto dto = CommentDto.builder().text("Great").build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> {
            Comment c = i.getArgument(0);
            c.setId(100L);
            return c;
        });

        CommentDto result = itemService.addComment(user.getId(), item.getId(), dto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Great", result.getText());
    }

    @Test
    void addComment_noBooking_throws() {
        Item item = Item.builder().id(10L).owner(user).build();
        CommentDto dto = CommentDto.builder().text("Test").build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(false);

        assertThrows(ValidationException.class,
                () -> itemService.addComment(user.getId(), item.getId(), dto));
    }

    @Test
    void getAllByOwner_returnsItems() {
        Item item = Item.builder().id(10L).owner(user).available(true).build();
        when(itemRepository.findAllByOwnerId(user.getId())).thenReturn(List.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(anyLong(), any(), any())).thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(anyLong(), any(), any())).thenReturn(null);

        List<ItemDto> results = itemService.getAllByOwner(user.getId());

        assertEquals(1, results.size());
        assertEquals(item.getId(), results.get(0).getId());
    }
}
