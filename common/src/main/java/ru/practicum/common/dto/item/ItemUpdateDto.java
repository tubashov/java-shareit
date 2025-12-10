package ru.practicum.common.dto.item;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemUpdateDto {
    @Size(min = 1, max = 255, message = "Name length must be 1-255")
    private String name;

    @Size(max = 1000, message = "Description too long")
    private String description;

    private Boolean available;
}
