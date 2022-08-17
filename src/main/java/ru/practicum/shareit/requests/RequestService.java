package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoWithResponses;

import java.util.Collection;
import java.util.Optional;

public interface RequestService {
    ItemRequestDto addNewRequest(Long userId, ItemRequest request);

    Collection<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    Optional<ItemRequestDtoWithResponses> findRequestById(Long requestId, Long userId);

    Collection<ItemRequestDtoWithResponses> findRequestsByUser(Long userId);
}
