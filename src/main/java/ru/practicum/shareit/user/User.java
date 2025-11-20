package ru.practicum.shareit.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@ Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Column(nullable = false, length = 512, unique = true)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Format error")
    private String email;
}
