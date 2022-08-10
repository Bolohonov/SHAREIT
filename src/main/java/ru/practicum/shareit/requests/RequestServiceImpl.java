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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto addNewRequest(Long userId, ItemRequest request) {
        checkUser(userId);
        request.setRequestorId(userId);
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        Iterable<ItemRequest> requests = Collections.EMPTY_LIST;
        if (!from.equals(null) && !size.equals(null)) {
            checkUser(userId);
            checkParams(from, size);
            PageRequest pageRequest = PageRequest.of(this.getPageNumber(from, size), size,
                    Sort.by("created").descending());
            requests = itemRequestRepository.findAll(pageRequest); 
        }
        return itemRequestMapper.toItemRequestDto(requests);
    }

    @Override
    public Optional<ItemRequestDtoWithResponses> findRequestById(Long requestId, Long userId) {
        checkUser(userId);
        ItemRequestDto requestDto = itemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId).get());
        return Optional.ofNullable(itemRequestMapper.toItemRequestDtoWithResponses(requestDto,
                itemService.findItemsByRequest(requestDto.getId())));
    }

    @Override
    public Collection<ItemRequestDtoWithResponses> findRequestsByUser(Long userId) {
        checkUser(userId);
        Collection<ItemRequestDtoWithResponses> requestsWithResponses = new ArrayList<>();
        Collection<ItemRequestDto> requests = getAllRequests(userId, 0, 100);

        for (ItemRequestDto itemRequestDto : requests) {
            requestsWithResponses.add(itemRequestMapper.toItemRequestDtoWithResponses(itemRequestDto,
                    itemService.findItemsByRequest(itemRequestDto.getId())));
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
        return from % size + 1;
    }
}
