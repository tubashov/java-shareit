package ru.practicum.common.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    @Size(min = 1, max = 255, message = "Name length must be 1-255")
    private String name;

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email too long")
    private String email;
}