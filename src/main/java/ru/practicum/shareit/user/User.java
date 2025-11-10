package ru.practicum.shareit.user;

/**
 * TODO Sprint add-controllers.
 */
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class User {
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Format error")
    private String email;
}
