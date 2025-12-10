package ru.practicum.gateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.common.dto.item.ItemDto;
import ru.practicum.common.dto.item.CommentDto;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Nice item")
                .build();
    }

    @Test
    void createItem_validInput_returnsOk() throws Exception {
        when(itemClient.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateItem_invalidId_returnsBadRequest() throws Exception {
        mockMvc.perform(patch("/items/0")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void updateItem_validInput_returnsOk() throws Exception {
        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getItem_invalidId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/items/0")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_invalidId_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/items/0/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_validInput_returnsOk() throws Exception {
        when(itemClient.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByOwner_returnsOk() throws Exception {
        when(itemClient.getAllByOwner(1L))
                .thenReturn(org.springframework.http.ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_withText_returnsOk() throws Exception {
        when(itemClient.searchItems("test"))
                .thenReturn(org.springframework.http.ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_emptyText_returnsOk() throws Exception {
        when(itemClient.searchItems(""))
                .thenReturn(org.springframework.http.ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk());
    }
}
