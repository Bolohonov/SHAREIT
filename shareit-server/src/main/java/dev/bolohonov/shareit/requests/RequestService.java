package dev.bolohonov.shareit.requests;

import dev.bolohonov.shareit.requests.dto.ItemRequestDto;
import dev.bolohonov.shareit.requests.dto.ItemRequestDtoWithResponses;

import java.util.Collection;
import java.util.Optional;

public interface RequestService {
    ItemRequestDto addNewRequest(Long userId, ItemRequest request);

    Collection<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    Optional<ItemRequestDtoWithResponses> findRequestById(Long requestId, Long userId);

    Collection<ItemRequestDtoWithResponses> findRequestsByUser(Long userId);
}
