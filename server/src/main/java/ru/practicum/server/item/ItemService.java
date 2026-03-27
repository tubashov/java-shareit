package ru.practicum.server.item;

import ru.practicum.common.dto.item.CommentDto;
import ru.practicum.common.dto.item.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    Item update(Long itemId, ItemDto updates, Long ownerId);

    ItemDto getById(Long itemId, Long userId);

    List<ItemDto> getAllByOwner(Long ownerId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto dto);
}