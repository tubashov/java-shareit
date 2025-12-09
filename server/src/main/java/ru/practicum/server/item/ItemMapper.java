package ru.practicum.server.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.common.dto.booking.BookingShortDto;
import ru.practicum.common.dto.item.ItemDto;
import ru.practicum.common.dto.item.CommentDto;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static ItemDto toItemDto(Item item,
                                    BookingShortDto last,
                                    BookingShortDto next,
                                    List<CommentDto> comments) {
        if (item == null) {
            return null;
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments != null ? comments : List.of())
                .build();
    }

    public static Item toItem(ItemDto dto, User owner) {
        if (dto == null) {
            return null;
        }

        ItemRequest request = null;
        if (dto.getRequestId() != null) {
            request = ItemRequest.builder().id(dto.getRequestId()).build();
        }

        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .request(request)
                .build();
    }
}
