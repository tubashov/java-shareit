package ru.practicum.server.user;

import ru.practicum.common.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(Long id, UserUpdateDto userUpdateDto);

    User getById(Long id);

    List<User> getAll();

    void delete(Long id);
}