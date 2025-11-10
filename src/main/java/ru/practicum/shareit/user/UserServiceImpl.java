package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1L;

    @Override
    public User create(User user) {
        // Проверка: email не должен повторяться
        boolean emailExists = users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
        if (emailExists) {
            throw new EmailAlreadyExistsException("User with that email is already exist");
        }

        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long id, User user) {
        User existing = users.get(id);
        if (existing == null) {
            // Лучше использовать кастомное исключение, которое обрабатывается в GlobalExceptionHandler
            throw new UserNotFoundException("User with id " + id + " not found");
        }

        // Проверка на дублирование email (если поле меняется)
        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(existing.getEmail())) {
            boolean emailExists = users.values().stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
            if (emailExists) {
                throw new EmailAlreadyExistsException("User with that email is already exist");
            }
            existing.setEmail(user.getEmail());
        }

        // Обновление имени (если передано)
        if (user.getName() != null && !user.getName().isBlank()) {
            existing.setName(user.getName());
        }

        return existing;
    }

    @Override
    public User getById(Long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}
