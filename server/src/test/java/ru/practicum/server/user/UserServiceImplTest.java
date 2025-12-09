package ru.practicum.server.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.server.exception.EmailAlreadyExistsException;
import ru.practicum.server.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
    }

    @Test
    void create_success() {
        when(userRepository.existsByEmailIgnoreCase(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = userService.create(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void create_existingEmail_throws() {
        when(userRepository.existsByEmailIgnoreCase(user.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.create(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_success() {
        User updates = new User();
        updates.setName("Jane Doe");
        updates.setEmail("jane@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCase("jane@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.update(1L, updates);

        assertEquals("Jane Doe", result.getName());
        assertEquals("jane@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_nonExistentUser_throws() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(2L, user));
    }

    @Test
    void update_emailExists_throws() {
        User updates = new User();
        updates.setEmail("existing@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCase("existing@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.update(1L, updates));
    }

    @Test
    void getById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getById_nonExistent_throws() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(2L));
    }

    @Test
    void getAll_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAll();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    void delete_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.delete(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistent_throws() {
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.delete(2L));
    }
}
