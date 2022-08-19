package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public ItemRequestDto addNewRequest(Long userId, ItemRequest request) {
        checkUser(userId);
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(request));
    }

    @Override
    public Collection<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        checkUser(userId);
        checkParams(from, size);
        PageRequest pageRequest = PageRequest.of(this.getPageNumber(from, size), size,
                Sort.by("created").descending());
        Iterable<ItemRequest> requests = itemRequestRepository.findAll(pageRequest);
        return itemRequestMapper.toItemRequestDto(requests);
    }

    @Override
    public Optional<ItemRequestDtoWithResponses> findRequestById(Long requestId, Long userId) {
        checkUser(userId);
        ItemRequest itemRequest = itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(NOT_FOUND);
                });
        return Optional.ofNullable(itemRequestMapper.toItemRequestDtoWithResponses(itemRequest,
                itemService.findItemsByRequest(requestId)));
    }

    @Override
    public Collection<ItemRequestDtoWithResponses> findRequestsByUser(Long userId) {
        checkUser(userId);
        Collection<ItemRequestDtoWithResponses> requestsWithResponses = new ArrayList<>();
        Collection<ItemRequest> requests = itemRequestRepository.findByRequesterId(userId);
        for (ItemRequest itemRequest : requests) {
            requestsWithResponses.add(itemRequestMapper.toItemRequestDtoWithResponses(itemRequest,
                    itemService.findItemsByRequest(itemRequest.getId())));
        }
        return requestsWithResponses;
    }

    private void checkUser(Long userId) {
        if (!userService.getUserById(userId).isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    private void checkParams(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
    }

    private Integer getPageNumber(Integer from, Integer size) {
        return from % size;
    }
}
