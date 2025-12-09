package ru.practicum.common.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestResponseDto {
    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}
