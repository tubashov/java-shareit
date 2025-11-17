package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private long nextId = 1L;

    @Override
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        boolean emailExists = users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));

        if (emailExists) {
            throw new EmailAlreadyExistsException("User with that email already exists");
        }

        user.setId(nextId++);
        users.put(user.getId(), user);

        log.info("Created user: {}", user);
        return user;
    }

    @Override
    public User update(Long id, User updates) {
        if (id == null || updates == null) {
            throw new IllegalArgumentException("Id and update data must not be null");
        }

        User existing = Optional.ofNullable(users.get(id))
                .orElseThrow(() -> NotFoundException.of("User", id));

        // Проверка уникальности email, если он меняется
        Optional.ofNullable(updates.getEmail())
                .filter(email -> !email.equalsIgnoreCase(existing.getEmail()))
                .ifPresent(newEmail -> {
                    boolean emailExists = users.values().stream()
                            .anyMatch(u -> u.getEmail().equalsIgnoreCase(newEmail));
                    if (emailExists) {
                        throw new EmailAlreadyExistsException("User with that email already exists");
                    }
                    existing.setEmail(newEmail);
                });

        // Обновление имени
        Optional.ofNullable(updates.getName())
                .filter(name -> !name.isBlank())
                .ifPresent(existing::setName);

        log.info("Updated user {}: {}", id, existing);
        return existing;
    }

    @Override
    public User getById(Long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> NotFoundException.of("User", id));
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        User removed = users.remove(id);
        if (removed == null) {
            throw NotFoundException.of("User", id);
        }

        log.info("Deleted user with id {}", id);
    }
}
