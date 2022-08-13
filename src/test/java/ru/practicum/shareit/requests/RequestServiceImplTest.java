package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDtoForItem;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

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
    void addNewRequest() {
    }

    @Test
    void getAllRequests() {
    }

    @Test
    void findRequestById() {
    }

    @Test
    void findRequestsByUser() {
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

    private Item makeItem(Long id, String name, String description, Boolean available, Long ownerId) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        return item;
    }

    private ItemDto makeItemDto(Long id, String name, String description, Boolean available,
                                Long requestId, Collection<Comment> comments) {
        ItemDto item = new ItemDto(id, name, description, available, requestId, comments);
        return item;
    }

    private ItemDtoWithBooking makeItemDtoWithBooking(Long id, String name, String description, Boolean available,
                                                      Long request, ItemDtoWithBooking.Booking lastBooking,
                                                      ItemDtoWithBooking.Booking nextBooking,
                                                      Collection<CommentDtoForItem> comments) {
        ItemDtoWithBooking item = new ItemDtoWithBooking(id, name, description, available, request,
                lastBooking, nextBooking, comments);
        return item;
    }

    private Comment makeComment(Long id, String text, Long authorId, Long itemId, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setAuthorId(authorId);
        comment.setItemId(itemId);
        comment.setCreated(created);
        return comment;
    }

    private Booking makeBooking(Long id, LocalDateTime start, LocalDateTime end, Long itemId, Long bookerId,
                                Status status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItemId(itemId);
        booking.setBookerId(bookerId);
        booking.setStatus(status);
        return booking;
    }

    private BookingDto makeBookingDto(Long id, LocalDateTime start, LocalDateTime end, Long itemId, Long bookerId,
                                      Status status, String itemName) {
        BookingDto booking = new BookingDto(id, start, end, new BookingDto.Item(itemId, itemName),
                new BookingDto.Booker(bookerId), status);
        return booking;
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