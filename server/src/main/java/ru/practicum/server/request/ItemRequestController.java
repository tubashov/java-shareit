package ru.practicum.server.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.item.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService service;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody @Valid ItemRequestDto dto
    ) {
        return service.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwn(@RequestHeader(USER_HEADER) Long userId) {
        return service.getOwn(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id
    ) {
        return service.getById(userId, id);
    }
}
