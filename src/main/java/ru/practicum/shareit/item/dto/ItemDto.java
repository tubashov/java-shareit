package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Data
@Builder
@Getter
@Setter
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

    private BookingShortDto lastBooking; // последнее завершённое бронирование

    private BookingShortDto nextBooking; // ближайшее будущее бронирование

    private List<CommentDto> comments;
}
