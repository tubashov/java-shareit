package ru.practicum.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.common.dto.item.ItemShortDto;
import ru.practicum.common.dto.item.ItemRequestDto;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest request = ItemRequest.builder()
                .description(dto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();

        request = requestRepository.save(request);

        return ItemRequestMapper.toDto(request, List.of());
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(r -> ItemRequestMapper.toDto(r, getItemsForRequest(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, int from, int size) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<ItemRequest> requests =
                requestRepository.findAllOthers(userId, PageRequest.of(from / size, size));

        return requests.stream()
                .map(r -> ItemRequestMapper.toDto(r, getItemsForRequest(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        return ItemRequestMapper.toDto(request, getItemsForRequest(requestId));
    }

    private List<ItemShortDto> getItemsForRequest(Long requestId) {
        return itemRepository.findByRequest_Id(requestId).stream()
                .map(i -> ItemShortDto.builder()
                        .id(i.getId())
                        .name(i.getName())
                        .ownerId(i.getOwner().getId())
                        .build())
                .collect(Collectors.toList());
    }
}
