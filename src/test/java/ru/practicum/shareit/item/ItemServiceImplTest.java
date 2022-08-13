package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoForItem;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.AccessToItemException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserService userService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemMapper itemMapper;
    @Mock
    CommentMapper commentMapper;

    @Test
    @SneakyThrows
    void testCreateItemSuccess() {
        User user = makeUser(1L, "Ivan", "ivan@yandex.ru");
        Item firstItem = makeItem(1L, "Отвертка", "Для откручивания",
                true, 1L);
        ItemDto dtoToCompare = makeItemDto(1L, "Отвертка",
                "Для откручивания", true, null, Collections.emptyList());
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(firstItem);
        ItemDto itemDtoResult = getItemService().addNewItem(1L, firstItem);
        itemDtoResult.setId(1L);
        assertEquals(itemDtoResult, dtoToCompare);
        Mockito
                .verify(userService, times(1)).getUserById(1L);
        Mockito
                .verify(itemRepository, times(1)).save(firstItem);
    }

    @Test
    void testCreateItemUserNotFoundException() {
        Item firstItem = makeItem(1L, "Отвертка", "Для откручивания",
                true, 1L);
        assertThrows(UserNotFoundException.class, () ->
                getItemService().addNewItem(1L, firstItem));
    }

    @Test
    @SneakyThrows
    void testUpdateItemSuccess() {
        User user = makeUser(1L, "Ivan", "ivan@yandex.ru");
        Item firstItem = makeItem(1L, "Отвертка", "Для откручивания",
                true, user.getId());
        Item updatedItem = makeItem(1L, "ОтверткаНовая", "Для откручивания",
                true, user.getId());
        ItemDto dtoToCompare = makeItemDto(1L, "ОтверткаНовая",
                "Для откручивания", true, null, Collections.emptyList());
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(updatedItem);
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(firstItem));
        ItemDto itemDtoResult = getItemService().updateItem(user.getId(), updatedItem).get();
        assertEquals(itemDtoResult, dtoToCompare);
    }

    @Test
    @SneakyThrows
    void testUpdateItemAccessToItemException() {
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userWrong = makeUser(2L, "Ivan2", "ivan2@yandex.ru");
        Item firstItem = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Item updatedItem = makeItem(1L, "ОтверткаНовая", "Для откручивания",
                true, userOwner.getId());
        ItemDto dtoToCompare = makeItemDto(1L, "ОтверткаНовая",
                "Для откручивания", true, null, Collections.emptyList());
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(firstItem));
        assertThrows(AccessToItemException.class, () ->
                getItemService().updateItem(userWrong.getId(), updatedItem));
    }

    @Test
    @SneakyThrows
    void testGetItemByIdSuccess() {
        User user = makeUser(1L, "Ivan", "ivan@yandex.ru");
        Item firstItem = makeItem(1L, "Отвертка", "Для откручивания",
                true, user.getId());
        ItemDtoWithBooking dto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true,
                null,
                null,
                null,
                Collections.emptyList()
        );
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(firstItem));
        Mockito
                .when(itemMapper.toItemDtoWithBooking(any(Item.class), any(), any(), anyCollection()))
                .thenReturn(dto);
        ItemDtoWithBooking itemDtoResult = getItemService().findItemById(firstItem.getId(), user.getId()).get();
        assertEquals(itemDtoResult.getId(), firstItem.getId());
        assertEquals(itemDtoResult.getName(), firstItem.getName());
        assertEquals(itemDtoResult.getDescription(), firstItem.getDescription());
        assertEquals(itemDtoResult.getAvailable(), firstItem.getAvailable());
    }

    @Test
    void testGetItemByIdUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () ->
                getItemService().findItemById(1L, 1L));
    }

    @Test
    void testGetItemByIdItemNotFoundException() {
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.ofNullable(userOwner));
        assertThrows(ItemNotFoundException.class, () ->
                getItemService().findItemById(1L, 1L));
    }

    @Test
    void testGetAllUserItems() {
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item firstItem = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Item secondItem = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        ItemDtoWithBooking firstDtoItem = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, null,
                null, Collections.emptyList());
        ItemDtoWithBooking secondDtoItem = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, null,
                null, Collections.emptyList());
        List<Item> itemsList = new ArrayList<>();
        itemsList.add(firstItem);
        itemsList.add(secondItem);
        Page<Item> items = new PageImpl<>(itemsList);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userOwner));
        Mockito
                .when(itemRepository.findByOwnerId(userOwner.getId(), PageRequest.of(0, 10,
                        Sort.by("id").ascending())))
                .thenReturn(items);
        Mockito
                .when(itemMapper.toItemDtoWithBooking(firstItem, Optional.empty(), Optional.empty(),
                        Collections.emptyList()))
                .thenReturn(firstDtoItem);
        Mockito
                .when(itemMapper.toItemDtoWithBooking(secondItem, Optional.empty(), Optional.empty(),
                        Collections.emptyList()))
                .thenReturn(secondDtoItem);
        Collection<ItemDtoWithBooking> itemsResult = getItemService().getUserItems(1L, 0, 10);
        List<ItemDtoWithBooking> result = itemsResult.stream()
                .sorted(Comparator.comparingLong(i -> i.getId()))
                .collect(Collectors.toList());
        assertEquals(result.get(0), firstDtoItem);
        assertEquals(result.get(1), secondDtoItem);
    }

    @Test
    @SneakyThrows
    void testAddCommentSuccess() {
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item firstItem = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Item secondItem = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        ItemDtoWithBooking firstDtoItem = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, null,
                null, Collections.emptyList());
        ItemDtoWithBooking secondDtoItem = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, null,
                null, Collections.emptyList());
        Booking booking = makeBooking(1L, LocalDateTime.of(2022, 8, 10, 10, 0),
                LocalDateTime.of(2022, 8, 11, 10, 0), 1L, userBooker.getId(),
                Status.CANCELED);
        Comment comment = makeComment(1L, "Все ок", userBooker.getId(),
                firstItem.getId(), LocalDateTime.of(2022, 8, 12, 10, 0));
        CommentDto commentDto = new CommentDto(comment.getId(), comment.getText(),
                comment.getItemId(), comment.getAuthorId(), comment.getCreated(), "Pasha");
        List<Booking> listBooking = new ArrayList<>();
        listBooking.add(booking);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findBookingByItemIdAndAndBookerId(anyLong(), anyLong(), any()))
                .thenReturn(listBooking);
        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(comment);
        CommentDto result = getItemService().addComment(userBooker.getId(), firstItem.getId(), comment);
        assertEquals(result.getId(), commentDto.getId());
        assertEquals(result.getText(), commentDto.getText());
        assertEquals(result.getAuthorName(), commentDto.getAuthorName());
    }

    private ItemService getItemService() {
        return new ItemServiceImpl(itemRepository, itemMapper, userService,
                bookingRepository, commentRepository, commentMapper);
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
}