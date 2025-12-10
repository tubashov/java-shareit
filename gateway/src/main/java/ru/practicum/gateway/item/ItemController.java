package ru.practicum.gateway.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.item.ItemDto;
import ru.practicum.common.dto.item.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody @Valid ItemDto itemDto
    ) {
        log.info("Gateway: createItem for userId={}", userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto
    ) {
        if (!isValidId(itemId)) {
            return ResponseEntity.badRequest().body("Id must be positive");
        }
        log.info("Gateway: updateItem {} for userId={}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long itemId
    ) {
        if (!isValidId(itemId)) {
            return ResponseEntity.badRequest().body("Id must be positive");
        }
        log.info("Gateway: getItem {} for userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader(USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Gateway: getAllByOwner for userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        log.info("Gateway: searchItems with text={}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentDto commentDto
    ) {
        if (!isValidId(itemId)) {
            return ResponseEntity.badRequest().body("Id must be positive");
        }
        log.info("Gateway: addComment for itemId={}, userId={}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

    private boolean isValidId(Long id) {
        return id != null && id > 0;
    }
}
