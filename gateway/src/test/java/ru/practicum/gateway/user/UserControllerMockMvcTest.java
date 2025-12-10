package ru.practicum.gateway.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.common.dto.user.UserDto;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    private UserDto userDto;

    @BeforeEach
    void setup() {
        userDto = new UserDto();
        userDto.setName("John");
        userDto.setEmail("john@example.com");
    }

    @Test
    void createUser_validInput_returnsOk() throws Exception {
        Mockito.when(userClient.createUser(any())).thenReturn(ResponseEntity.ok("created"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("created"));
    }

    @Test
    void getUser_validId_returnsOk() throws Exception {
        Mockito.when(userClient.getUser(anyLong())).thenReturn(ResponseEntity.ok("user"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("user"));
    }

    @Test
    void getUser_invalidId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/users/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(containsString("positive")));
    }

    @Test
    void getUser_nullId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/users/"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_returnsOk() throws Exception {
        Mockito.when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok("allUsers"));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("allUsers"));
    }

    @Test
    void updateUser_validInput_returnsOk() throws Exception {
        Mockito.when(userClient.updateUser(anyLong(), any())).thenReturn(ResponseEntity.ok("updated"));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("updated"));
    }

    @Test
    void updateUser_invalidId_returnsBadRequest() throws Exception {
        mockMvc.perform(patch("/users/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(containsString("positive")));
    }

    @Test
    void deleteUser_validId_returnsOk() throws Exception {
        Mockito.when(userClient.deleteUser(anyLong())).thenReturn(ResponseEntity.ok("deleted"));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted"));
    }

    @Test
    void deleteUser_invalidId_returnsBadRequest() throws Exception {
        mockMvc.perform(delete("/users/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(containsString("positive")));
    }
}
