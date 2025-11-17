package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private long nextId = 1;

    private final UserService userService; // Внедрение UserService

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = Optional.ofNullable(userService.getById(userId))
                .orElseThrow(() -> NotFoundException.of("User", userId));

        Item item = ItemMapper.toItem(itemDto, owner);
        item.setAvailable(Optional.ofNullable(itemDto.getAvailable()).orElse(true));

        item.setId(nextId++);
        items.put(item.getId(), item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public Item update(Long itemId, ItemDto updates, User owner) {
        if (updates == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        Long userId = owner != null ? owner.getId() : null;

        Long ownerId = Optional.ofNullable(userId)
                .orElseThrow(() -> NotFoundException.of("User", userId));

        Item existing = Optional.ofNullable(items.get(itemId))
                .orElseThrow(() -> NotFoundException.of("Item", itemId));

        if (existing.getOwner() == null || !Objects.equals(existing.getOwner().getId(), ownerId)) {
            throw new IllegalStateException("Only owner can edit this item");
        }

        Optional.ofNullable(updates.getName()).ifPresent(existing::setName);
        Optional.ofNullable(updates.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(updates.getAvailable()).ifPresent(existing::setAvailable);

        log.info("Item updated: {}", existing);
        return existing;
    }

    @Override
    public Item getById(Long itemId) {
        return Optional.ofNullable(items.get(itemId))
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
    }

    @Override
    public List<Item> getAllByOwner(Long ownerId) {
        return Optional.ofNullable(ownerId)
                .map(id -> items.values().stream()
                        .filter(item -> item.getOwner() != null && Objects.equals(item.getOwner().getId(), id))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();

        String lowerText = text.toLowerCase();

        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()) &&
                        ((item.getName() != null && item.getName().toLowerCase().contains(lowerText)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerText))))
                .collect(Collectors.toList());
    }
}
