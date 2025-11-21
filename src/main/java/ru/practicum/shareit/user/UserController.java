package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping(produces = "application/json")
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        var createdUser = userService.create(UserMapper.toUser(userDto));
        var createdUserDto = UserMapper.toUserDto(createdUser);
        log.info("Create user: {}", createdUserDto);
        return createdUserDto;
    }

    @GetMapping(produces = "application/json")
    public List<UserDto> getAllUsers() {
        var users = userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("GET /users - request all users, count: {}", users.size());
        return users;
    }

    @GetMapping(value = "/{userId}", produces = "application/json")
    public UserDto getUserById(@PathVariable @Positive Long userId) {
        var userDto = UserMapper.toUserDto(userService.getById(userId));
        log.info("GET /users/{} - user request by id", userId);
        return userDto;
    }

    @PatchMapping(value = "/{userId}", produces = "application/json")
    public UserDto updateUser(@PathVariable @Positive Long userId, @RequestBody UserDto userDto) {
        var updatedUser = userService.update(userId, UserMapper.toUser(userDto));
        var updatedUserDto = UserMapper.toUserDto(updatedUser);
        log.info("PATCH /users/{} - update user: {}", userId, updatedUserDto);
        return updatedUserDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Positive Long userId) {
        userService.delete(userId);
        log.info("DELETE /users/{} - user deleted", userId);
    }
}
