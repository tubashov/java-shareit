package ru.practicum.common.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemUpdateDto {
    private String name;

    private String description;

    private Boolean available;
}
