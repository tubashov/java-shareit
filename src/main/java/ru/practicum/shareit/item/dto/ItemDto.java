package ru.practicum.shareit.item.dto;

/**
 * TODO Sprint add-controllers.
 */
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ItemDto {
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Available must not be null")
    private Boolean available;

    private Long ownerId;
    private Long requestId;
}
