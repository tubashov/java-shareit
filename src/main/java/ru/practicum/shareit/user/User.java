package ru.practicum.shareit.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Column(nullable = false, length = 512, unique = true)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Format error")
    private String email;
}
