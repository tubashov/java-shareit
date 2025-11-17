package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    Item update(Long itemId, ItemDto updates, User owner);

    Item getById(Long id);

    List<Item> getAllByOwner(Long ownerId);

    List<Item> search(String text);
}