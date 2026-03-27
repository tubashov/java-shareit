package ru.practicum.server.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingRepository;
import ru.practicum.common.dto.booking.BookingStatus;
import ru.practicum.server.comment.Comment;
import ru.practicum.server.comment.CommentRepository;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void testItemRepositoryCrud() {
        User user = userRepository.save(new User(null, "Alice", "alice@mail.com"));
        Item item = itemRepository.save(new Item(null, "Laptop", "Dell laptop", true, user, null));

        Item found = itemRepository.findById(item.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Laptop", found.getName());

        List<Item> itemsByOwner = itemRepository.findAllByOwnerId(user.getId());
        assertEquals(1, itemsByOwner.size());
    }

    @Test
    void testBookingRepositoryQueries() {
        User user = userRepository.save(new User(null, "Bob", "bob@mail.com"));
        Item item = itemRepository.save(new Item(null, "Phone", "iPhone", true, user, null));

        Booking booking = bookingRepository.save(
                Booking.builder()
                        .item(item)
                        .booker(user)
                        .start(LocalDateTime.now().minusDays(1))
                        .end(LocalDateTime.now().plusDays(1))
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        Booking lastBooking = bookingRepository
                .findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(item.getId(), BookingStatus.APPROVED, LocalDateTime.now().plusDays(2));

        assertNotNull(lastBooking);
        assertEquals(item.getId(), lastBooking.getItem().getId());
    }

    @Test
    void testCommentRepository() {
        User user = userRepository.save(new User(null, "Eve", "eve@mail.com"));
        Item item = itemRepository.save(new Item(null, "Tablet", "Samsung tablet", true, user, null));

        Comment comment = commentRepository.save(Comment.builder()
                .text("Great!")
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build());

        List<Comment> comments = commentRepository.findByItemId(item.getId());
        assertEquals(1, comments.size());
        assertEquals("Great!", comments.get(0).getText());
    }
}
