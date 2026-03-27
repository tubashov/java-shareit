package ru.practicum.server.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exception.EmailAlreadyExistsException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.common.dto.user.UserUpdateDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User create(User user) {
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new EmailAlreadyExistsException("User with that email already exists");
        }

        User saved = userRepository.save(user);
        log.info("Created user: {}", saved);
        return saved;
    }

    @Override
    @Transactional
    public User update(Long userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("User", userId));

        if (userUpdateDto.getName() != null) {
            user.setName(userUpdateDto.getName());
        }

        if (userUpdateDto.getEmail() != null) {
            if (!user.getEmail().equals(userUpdateDto.getEmail()) &&
                    userRepository.existsByEmailIgnoreCase(userUpdateDto.getEmail())) {
                throw new EmailAlreadyExistsException("User with that email already exists");
            }
            user.setEmail(userUpdateDto.getEmail());
        }

        return userRepository.save(user);
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
