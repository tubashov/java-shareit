package ru.practicum.server.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.common.dto.user.UserDto;
import ru.practicum.common.dto.item.*;
import ru.practicum.common.dto.booking.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private JacksonTester<UserDto> jsonUserDto;
    private JacksonTester<ItemDto> jsonItemDto;
    private JacksonTester<CommentDto> jsonCommentDto;
    private JacksonTester<ItemUpdateDto> jsonItemUpdateDto;
    private JacksonTester<ItemShortDto> jsonItemShortDto;
    private JacksonTester<ItemRequestResponseDto> jsonItemRequestResponseDto;
    private JacksonTester<BookingDto> jsonBookingDto;
    private JacksonTester<BookingShortDto> jsonBookingShortDto;
    private JacksonTester<BookingResponseDto> jsonBookingResponseDto;
    private JacksonTester<BookItemRequestDto> jsonBookItemRequestDto;

    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    void testUserDtoSerialization() throws Exception {
        UserDto user = new UserDto(1L, "John Doe", "john@example.com");
        String json = jsonUserDto.write(user).getJson();
        UserDto deserialized = jsonUserDto.parse(json).getObject();
        assertThat(deserialized).isEqualTo(user);
    }

    @Test
    void testItemDtoSerialization() throws Exception {
        CommentDto comment = new CommentDto(1L, "Nice!", "Jane", LocalDateTime.now());
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Item1")
                .description("Desc1")
                .available(true)
                .ownerId(1L)
                .comments(List.of(comment))
                .build();
        String json = jsonItemDto.write(item).getJson();
        ItemDto deserialized = jsonItemDto.parse(json).getObject();
        assertThat(deserialized).isEqualTo(item);
    }

    @Test
    void testCommentDtoSerialization() throws Exception {
        CommentDto comment = new CommentDto(1L, "Nice!", "Jane", LocalDateTime.now());
        String json = jsonCommentDto.write(comment).getJson();
        CommentDto deserialized = jsonCommentDto.parse(json).getObject();
        assertThat(deserialized).isEqualTo(comment);
    }

    @Test
    void testItemUpdateDtoSerialization() throws Exception {
        ItemUpdateDto update = new ItemUpdateDto("NewName", "NewDesc", true);
        String json = jsonItemUpdateDto.write(update).getJson();
        ItemUpdateDto deserialized = jsonItemUpdateDto.parse(json).getObject();
        assertThat(deserialized).isEqualTo(update);
    }

    @Test
    void testItemShortDtoSerialization() throws Exception {
        ItemShortDto shortDto = new ItemShortDto(1L, "Item1", 2L);
        String json = jsonItemShortDto.write(shortDto).getJson();
        ItemShortDto deserialized = jsonItemShortDto.parse(json).getObject();
        assertThat(deserialized).isEqualTo(shortDto);
    }

    @Test
    void testItemRequestResponseDtoSerialization() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Item1")
                .description("Desc1")
                .available(true)
                .ownerId(1L)
                .build();
        ItemRequestResponseDto requestResponse = new ItemRequestResponseDto(1L, "RequestDesc", LocalDateTime.now(), List.of(item));
        String json = jsonItemRequestResponseDto.write(requestResponse).getJson();
        ItemRequestResponseDto deserialized = jsonItemRequestResponseDto.parse(json).getObject();
        assertThat(deserialized).isEqualTo(requestResponse);
    }

    @Test
    void testBookingDtoSerialization() throws Exception {
        BookingDto booking = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .bookerId(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .build();
        String json = jsonBookingDto.write(booking).getJson();
        BookingDto deserialized = jsonBookingDto.parse(json).getObject();
        assertThat(deserialized).isEqualTo(booking);
    }

    @Test
    void testBookingShortDtoSerialization() throws Exception {
        BookingShortDto shortDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();
        String json = jsonBookingShortDto.write(shortDto).getJson();
        BookingShortDto deserialized = jsonBookingShortDto.parse(json).getObject();
        assertThat(deserialized).isEqualTo(shortDto);
    }

    @Test
    void testBookingResponseDtoSerialization() throws Exception {
        BookingResponseDto.BookerDto booker = BookingResponseDto.BookerDto.builder().id(2L).build();
        BookingResponseDto.ItemInBookingDto item = BookingResponseDto.ItemInBookingDto.builder().id(1L).name("Item1").build();
        BookingResponseDto response = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .item(item)
                .build();
        String json = jsonBookingResponseDto.write(response).getJson();
        BookingResponseDto deserialized = jsonBookingResponseDto.parse(json).getObject();
        assertThat(deserialized).isEqualTo(response);
    }

    @Test
    void testBookItemRequestDtoSerialization() throws Exception {
        BookItemRequestDto request = new BookItemRequestDto(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        String json = jsonBookItemRequestDto.write(request).getJson();
        BookItemRequestDto deserialized = jsonBookItemRequestDto.parse(json).getObject();
        assertThat(deserialized).isEqualTo(request);
    }
}
