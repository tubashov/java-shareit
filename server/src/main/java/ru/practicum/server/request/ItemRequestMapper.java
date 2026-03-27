package ru.practicum.server.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.common.dto.item.ItemRequestDto;
import ru.practicum.common.dto.item.ItemShortDto;
import ru.practicum.server.user.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest request, List<ItemShortDto> items) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }

    public static ItemRequest fromDto(ItemRequestDto dto, User requester) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }
}

