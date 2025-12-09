package ru.practicum.gateway.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.client.ClientRestFactory;
import ru.practicum.common.dto.item.ItemDto;
import ru.practicum.common.dto.item.CommentDto;

import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(ClientRestFactory.build(serverUrl + API_PREFIX, builder));
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        log.info("Gateway: createItem request {}", itemDto);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Gateway: updateItem request {} for itemId={}", itemDto, itemId);
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        log.info("Gateway: getItem request for itemId={}", itemId);
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllByOwner(Long userId) {
        log.info("Gateway: getAllByOwner request for userId={}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        log.info("Gateway: searchItems request with text={}", text);
        Map<String, Object> params = Map.of("text", text);
        return get("/search?text={text}", null, params);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Gateway: addComment request {} for itemId={}", commentDto, itemId);
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
