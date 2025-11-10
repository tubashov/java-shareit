package ru.practicum.shareit.item;

/**
 * TODO Sprint add-controllers.
 */
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available = true;
    private User owner;
    private Long requestId;
}
