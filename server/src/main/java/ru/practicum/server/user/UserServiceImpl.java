package ru.practicum.server.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exception.EmailAlreadyExistsException;
import ru.practicum.server.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new EmailAlreadyExistsException("User with that email already exists");
        }

        User saved = userRepository.save(user);
        log.info("Created user: {}", saved);
        return saved;
    }

    @Override
    @Transactional
    public User update(Long id, User updates) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("User", id));

        if (updates.getEmail() != null && !updates.getEmail().equalsIgnoreCase(existing.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(updates.getEmail())) {
                throw new EmailAlreadyExistsException("User with that email already exists");
            }
            existing.setEmail(updates.getEmail());
        }

        if (updates.getName() != null && !updates.getName().isBlank()) {
            existing.setName(updates.getName());
        }

        User saved = userRepository.save(existing);
        log.info("Updated user {}: {}", id, saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("User", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw NotFoundException.of("User", id);
        }
        userRepository.deleteById(id);
        log.info("Deleted user with id {}", id);
    }
}
