package ru.practicum.server.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingMapper;
import ru.practicum.server.booking.BookingRepository;
import ru.practicum.common.dto.booking.BookingStatus;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.common.dto.item.CommentDto;
import ru.practicum.server.comment.Comment;
import ru.practicum.server.comment.CommentMapper;
import ru.practicum.common.dto.item.ItemDto;
import ru.practicum.server.comment.CommentRepository;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.request.ItemRequestRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("User", userId));

        Item item = ItemMapper.toItem(itemDto, owner);

        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            ItemRequest req = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> NotFoundException.of("ItemRequest", requestId));
            item.setRequest(req);
        }

        Item saved = itemRepository.save(item);
        log.info("Created item: id={}, ownerId={}", saved.getId(), userId);

        return ItemMapper.toItemDto(saved);
    }

    @Transactional
    @Override
    public Item update(Long itemId, ItemDto updates, Long ownerId) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> NotFoundException.of("Item", itemId));

        if (!existing.getOwner().getId().equals(ownerId)) {
            throw NotFoundException.of("Item owner", ownerId);
        }

        Optional.ofNullable(updates.getName()).ifPresent(existing::setName);
        Optional.ofNullable(updates.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(updates.getAvailable()).ifPresent(existing::setAvailable);

        Item saved = itemRepository.save(existing);
        log.info("Updated item: id={}, ownerId={}", saved.getId(), ownerId);

        return saved;
    }

    @Override
    public ItemDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> NotFoundException.of("Item", itemId));

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toDto)
                .toList();

        Booking last = null;
        Booking next = null;

        if (item.getOwner().getId().equals(userId)) {
            last = bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                    itemId, BookingStatus.APPROVED, LocalDateTime.now());
            next = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                    itemId, BookingStatus.APPROVED, LocalDateTime.now());
        }

        ItemDto dto = ItemMapper.toItemDto(
                item,
                last != null ? BookingMapper.toShortDto(last) : null,
                next != null ? BookingMapper.toShortDto(next) : null,
                comments != null ? comments : List.of()
        );

        log.info("Retrieved item: id={}, userId={}", itemId, userId);
        return dto;
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);

        List<ItemDto> dtos = items.stream().map(item -> {
            ItemDto dto = ItemMapper.toItemDto(item);

            Booking lastBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                            item.getId(), BookingStatus.APPROVED, LocalDateTime.now());

            Booking nextBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(), BookingStatus.APPROVED, LocalDateTime.now());

            dto.setLastBooking(lastBooking != null ? BookingMapper.toShortDto(lastBooking) : null);
            dto.setNextBooking(nextBooking != null ? BookingMapper.toShortDto(nextBooking) : null);

            return dto;
        }).collect(Collectors.toList());

        log.info("Retrieved all items for ownerId={}, count={}", ownerId, dtos.size());
        return dtos;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();

        List<ItemDto> results = itemRepository
                .findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(
                        text, text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Searched items with text='{}', results={}", text, results.size());
        return results;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        boolean hasBooking = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                userId, itemId, LocalDateTime.now()
        );

        if (!hasBooking) {
            throw new ValidationException("User has not completed booking for this item");
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);
        log.info("Added comment: id={}, itemId={}, userId={}", saved.getId(), itemId, userId);

        return CommentMapper.toDto(saved);
    }
}
