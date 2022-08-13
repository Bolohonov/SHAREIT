package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDtoWithoutComments;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    ItemRequestMapper itemRequestMapper;
    @Mock
    UserService userService;
    @Mock
    ItemService itemService;

    @Test
    void testAddNewRequestSuccess() {
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        ItemRequest itemRequest = makeItemRequest(1L, "Нужна отвертка", userBooker.getId(),
                null);
        ItemRequestDto itemRequestDto = makeItemRequestDto(1L, "Нужна отвертка",
                LocalDateTime.now());
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto result = getRequestService().addNewRequest(userBooker.getId(), itemRequest);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
    }

    @Test
    void testAddNewRequestUserNotFoundException() {
        ItemRequest itemRequest = makeItemRequest(1L, "Нужна отвертка", 3L,
                null);
        assertThrows(UserNotFoundException.class, () -> getRequestService().addNewRequest(3L, itemRequest));
    }

    @Test
    void getAllRequests() {
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        ItemRequest itemRequest = makeItemRequest(1L, "Нужна отвертка", userBooker.getId(),
                null);
        ItemRequest itemRequestSecond = makeItemRequest(2L, "Нужна дрель", userBooker.getId(),
                null);
        ItemRequestDto itemRequestDto = makeItemRequestDto(1L, "Нужна отвертка",
                LocalDateTime.now());
        ItemRequestDto itemRequestDtoSecond = makeItemRequestDto(2L, "Нужна дрель",
                LocalDateTime.now());
        PageRequest pageRequest = PageRequest.of(0, 10,
                Sort.by("created").descending());
        Collection<ItemRequestDto> dtos = new ArrayList<>();
        dtos.add(itemRequestDto);
        dtos.add(itemRequestDtoSecond);
        List<ItemRequest> list = new ArrayList<>();
        list.add(itemRequest);
        list.add(itemRequestSecond);
        Page<ItemRequest> requests = new PageImpl<>(list);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(itemRequestRepository.findAll(pageRequest))
                .thenReturn(requests);
        Mockito
                .when(itemRequestMapper.toItemRequestDto(requests))
                .thenReturn(dtos);
        Collection<ItemRequestDto> result = getRequestService().getAllRequests(userBooker.getId(), 0, 10);
        assertEquals(result, dtos);
    }

    @Test
    void findRequestById() {
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        ItemRequest itemRequest = makeItemRequest(1L, "Нужна отвертка", userBooker.getId(),
                null);
        ItemRequest itemRequestSecond = makeItemRequest(2L, "Нужна дрель", userBooker.getId(),
                null);
        ItemRequestDto itemRequestDto = makeItemRequestDto(1L, "Нужна отвертка",
                LocalDateTime.now());
        ItemRequestDto itemRequestDtoSecond = makeItemRequestDto(2L, "Нужна дрель",
                LocalDateTime.now());
        PageRequest pageRequest = PageRequest.of(0, 10,
                Sort.by("created").descending());
        ItemRequestDtoWithResponses itemRequestDtoWithResponses = makeItemRequestWithResponses(1L,
                "Нужна отвертка", LocalDateTime.now(), null);
        ItemRequestDtoWithResponses itemRequestDtoWithResponsesSecond = makeItemRequestWithResponses(2L,
                "Нужна дрель", LocalDateTime.now(), null);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(itemService.findItemsByRequest(anyLong()))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequestSecond));
        ItemRequestDtoWithResponses result = getRequestService().findRequestById(itemRequestSecond.getId(),
                userBooker.getId()).get();
        assertEquals(result.getId(), itemRequestDtoWithResponsesSecond.getId());
        assertEquals(result.getDescription(), itemRequestDtoWithResponsesSecond.getDescription());
    }

    @Test
    void findRequestsByUser() {
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        ItemRequest itemRequest = makeItemRequest(1L, "Нужна отвертка", userBooker.getId(),
                null);
        ItemRequest itemRequestSecond = makeItemRequest(2L, "Нужна дрель", userBooker.getId(),
                null);
        ItemRequestDto itemRequestDto = makeItemRequestDto(1L, "Нужна отвертка",
                LocalDateTime.now());
        ItemRequestDto itemRequestDtoSecond = makeItemRequestDto(2L, "Нужна дрель",
                LocalDateTime.now());
        PageRequest pageRequest = PageRequest.of(0, 10,
                Sort.by("created").descending());
        ItemRequestDtoWithResponses itemRequestDtoWithResponses = makeItemRequestWithResponses(1L,
                "Нужна отвертка", LocalDateTime.now(), Collections.emptyList());
        ItemRequestDtoWithResponses itemRequestDtoWithResponsesSecond = makeItemRequestWithResponses(2L,
                "Нужна дрель", LocalDateTime.now(), Collections.emptyList());
        Collection<ItemRequestDtoWithResponses> dtos = new ArrayList<>();
        dtos.add(itemRequestDtoWithResponses);
        dtos.add(itemRequestDtoWithResponsesSecond);
        List<ItemRequest> list = new ArrayList<>();
        list.add(itemRequest);
        list.add(itemRequestSecond);
        Collection<ItemDtoWithoutComments> items = Collections.emptyList();
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(itemService.findItemsByRequest(anyLong()))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(itemRequestRepository.findByRequesterId(anyLong()))
                .thenReturn(list);
        Collection<ItemRequestDtoWithResponses> result = getRequestService()
                .findRequestsByUser(userBooker.getId());
        assertEquals(result.size(), dtos.size());
        assertEquals(result.stream().collect(Collectors.toList()).get(0).getId(),
                dtos.stream().collect(Collectors.toList()).get(0).getId());
        assertEquals(result.stream().collect(Collectors.toList()).get(1).getId(),
                dtos.stream().collect(Collectors.toList()).get(1).getId());
        assertEquals(result.stream().collect(Collectors.toList()).get(0).getDescription(),
                dtos.stream().collect(Collectors.toList()).get(0).getDescription());
        assertEquals(result.stream().collect(Collectors.toList()).get(1).getDescription(),
                dtos.stream().collect(Collectors.toList()).get(1).getDescription());
    }

    private RequestService getRequestService() {
        return new RequestServiceImpl(itemRequestRepository, itemRequestMapper, userService, itemService);
    }

    private User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemRequest makeItemRequest(Long id, String description,
                                        Long reqesterId, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequesterId(reqesterId);
        itemRequest.setCreated(created);
        return itemRequest;
    }

    private ItemRequestDto makeItemRequestDto(Long id, String description,
                                              LocalDateTime created) {
        return ItemRequestDto.builder()
                .id(id)
                .description(description)
                .created(created)
                .build();
    }

    private ItemRequestDtoWithResponses makeItemRequestWithResponses(Long id, String description,
                                                                     LocalDateTime created,
                                                                     Collection<ItemRequestDtoWithResponses
                                                                             .Response> items) {
        return ItemRequestDtoWithResponses.builder()
                .id(id)
                .description(description)
                .created(created)
                .items(items)
                .build();
    }
}