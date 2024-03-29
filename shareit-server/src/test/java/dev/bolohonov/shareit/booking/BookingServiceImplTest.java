package dev.bolohonov.shareit.booking;

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
import org.springframework.web.server.ResponseStatusException;
import dev.bolohonov.shareit.booking.dto.BookingDto;
import dev.bolohonov.shareit.booking.dto.BookingMapper;
import dev.bolohonov.shareit.booking.exceptions.AccessToBookingException;
import dev.bolohonov.shareit.booking.repository.BookingRepository;
import dev.bolohonov.shareit.comment.dto.CommentDtoForItem;
import dev.bolohonov.shareit.item.Item;
import dev.bolohonov.shareit.item.ItemService;
import dev.bolohonov.shareit.item.dto.ItemDtoWithBooking;
import dev.bolohonov.shareit.item.exceptions.AccessToItemException;
import dev.bolohonov.shareit.item.exceptions.ItemNotFoundException;
import dev.bolohonov.shareit.user.User;
import dev.bolohonov.shareit.user.UserService;
import dev.bolohonov.shareit.user.exceptions.UserNotFoundException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;

    @Test
    @SneakyThrows
    void testCreateBookingSuccess() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Booking bookingNext = makeBooking(2L, start.plusWeeks(5), end.plusWeeks(6),
                item.getId(), userBooker.getId(), Status.WAITING);
        BookingDto bookingDto = makeBookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItemId(),
                booking.getBookerId(), booking.getStatus(), item.getName());
        BookingDto bookingDtoNext = makeBookingDto(bookingNext.getId(), bookingNext.getStart(),
                bookingNext.getEnd(), bookingNext.getItemId(),
                bookingNext.getBookerId(), bookingNext.getStatus(), item.getName());
        ItemDtoWithBooking itemDto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, new ItemDtoWithBooking.Booking(booking.getId(), userBooker.getId()),
                new ItemDtoWithBooking.Booking(bookingNext.getId(), userBooker.getId()), Collections.emptyList());
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(itemDto));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingDto result = getBookingService().addNew(userBooker.getId(), booking);
        assertEquals(result.getId(), bookingDto.getId());
        assertEquals(result.getStart(), bookingDto.getStart());
        assertEquals(result.getEnd(), bookingDto.getEnd());
        assertEquals(result.getStatus(), bookingDto.getStatus());
        assertEquals(result.getBooker().getId(), bookingDto.getBooker().getId());
    }

    @Test
    void testCreateBookingUserNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        Booking booking = makeBooking(1L, start, end, 1L,
                1L, Status.WAITING);
        assertThrows(UserNotFoundException.class, () ->
                getBookingService().addNew(2L, booking));
    }

    @Test
    void testCreateBookingItemNotFound() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Booking booking = makeBooking(1L, start, end, 1L,
                userBooker.getId(), Status.WAITING);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        assertThrows(ItemNotFoundException.class, () ->
                getBookingService().addNew(2L, booking));
    }

    @Test
    void testCreateBookingStartDateIsBeforeNow() {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Booking bookingNext = makeBooking(2L, start.plusWeeks(5), end.plusWeeks(6),
                item.getId(), userBooker.getId(), Status.WAITING);

        ItemDtoWithBooking itemDto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, new ItemDtoWithBooking.Booking(booking.getId(), userBooker.getId()),
                new ItemDtoWithBooking.Booking(bookingNext.getId(), userBooker.getId()), Collections.emptyList());
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(itemDto));
        assertThrows(ResponseStatusException.class, () ->
                getBookingService().addNew(2L, booking));
    }

    @Test
    void testCreateBookingStartDateIsAfterEnd() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Booking bookingNext = makeBooking(2L, start.plusWeeks(5), end.plusWeeks(6),
                item.getId(), userBooker.getId(), Status.WAITING);

        ItemDtoWithBooking itemDto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, new ItemDtoWithBooking.Booking(booking.getId(), userBooker.getId()),
                new ItemDtoWithBooking.Booking(bookingNext.getId(), userBooker.getId()), Collections.emptyList());
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(itemDto));
        assertThrows(ResponseStatusException.class, () ->
                getBookingService().addNew(2L, booking));
    }

    @Test
    void testCreateBookingItemNotAvailable() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                false, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Booking bookingNext = makeBooking(2L, start.plusWeeks(5), end.plusWeeks(6),
                item.getId(), userBooker.getId(), Status.WAITING);
        ItemDtoWithBooking itemDto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                item.getAvailable(), null, new ItemDtoWithBooking.Booking(booking.getId(), userBooker.getId()),
                new ItemDtoWithBooking.Booking(bookingNext.getId(), userBooker.getId()), Collections.emptyList());
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(itemDto));
        assertThrows(ResponseStatusException.class, () ->
                getBookingService().addNew(2L, booking));

    }

    @Test
    void testApproveOrRejectBookingEmptyApproved() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        assertThrows(ResponseStatusException.class, () ->
                getBookingService().approveOrRejectBooking(1L, booking.getId(), null));
    }

    @Test
    void testApproveOrRejectBookingWrongId() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        assertThrows(ResponseStatusException.class, () ->
                getBookingService().approveOrRejectBooking(2L, 2L, true));
    }

    @Test
    void testApproveOrRejectBookingWrongStatus() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                false, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.APPROVED);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        assertThrows(ResponseStatusException.class, () ->
                getBookingService().approveOrRejectBooking(1L, 1L, true));
    }

    @Test
    void testApproveOrRejectBookingErrorCheckOwner() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(itemService.checkOwner(anyLong(),anyLong()))
                .thenReturn(false);
        assertThrows(AccessToItemException.class, () ->
                getBookingService().approveOrRejectBooking(10L, 1L, true));
    }

    @Test
    @SneakyThrows
    void testGetBookingByIdSuccess() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Booking bookingNext = makeBooking(2L, start.plusWeeks(5), end.plusWeeks(6),
                item.getId(), userBooker.getId(), Status.WAITING);
        BookingDto bookingDto = makeBookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItemId(),
                booking.getBookerId(), booking.getStatus(), item.getName());
        ItemDtoWithBooking itemDto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, new ItemDtoWithBooking.Booking(booking.getId(), userBooker.getId()),
                new ItemDtoWithBooking.Booking(bookingNext.getId(), userBooker.getId()), Collections.emptyList());
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(itemDto));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        BookingDto result = getBookingService().findBookingById(userBooker.getId(), booking.getId()).get();
        assertEquals(result.getId(), bookingDto.getId());
        assertEquals(result.getStart(), bookingDto.getStart());
        assertEquals(result.getEnd(), bookingDto.getEnd());
        assertEquals(result.getStatus(), bookingDto.getStatus());
        assertEquals(result.getBooker().getId(), bookingDto.getBooker().getId());
    }

    @Test
    @SneakyThrows
    void testGetBookingByIdAccessToBookingException() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        User userWrong = makeUser(3L, "Sasha", "sasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Booking bookingNext = makeBooking(2L, start.plusWeeks(5), end.plusWeeks(6),
                item.getId(), userBooker.getId(), Status.WAITING);
        BookingDto bookingDto = makeBookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItemId(),
                booking.getBookerId(), booking.getStatus(), item.getName());
        ItemDtoWithBooking itemDto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, new ItemDtoWithBooking.Booking(booking.getId(), userBooker.getId()),
                new ItemDtoWithBooking.Booking(bookingNext.getId(), userBooker.getId()), Collections.emptyList());
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        assertThrows(AccessToBookingException.class, () ->
                getBookingService().findBookingById(userWrong.getId(), booking.getId()).get());
    }

    @Test
    void testGetBookingByIdUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () ->
                getBookingService().findBookingById(1L, 1L));
    }

    @Test
    void testGetBookingByIdBookingNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Booking bookingNext = makeBooking(2L, start.plusWeeks(5), end.plusWeeks(6),
                item.getId(), userBooker.getId(), Status.WAITING);
        ItemDtoWithBooking itemDto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, new ItemDtoWithBooking.Booking(booking.getId(), userBooker.getId()),
                new ItemDtoWithBooking.Booking(bookingNext.getId(), userBooker.getId()), Collections.emptyList());
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () ->
                getBookingService().findBookingById(userBooker.getId(), 5L));
    }

    @Test
    @SneakyThrows
    void testGetUserBookingsStateAllSuccess() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Booking bookingNext = makeBooking(2L, start.plusWeeks(5), end.plusWeeks(6),
                item.getId(), userBooker.getId(), Status.WAITING);
        BookingDto bookingDto = makeBookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItemId(),
                booking.getBookerId(), booking.getStatus(), item.getName());
        BookingDto bookingDtoNext = makeBookingDto(bookingNext.getId(), bookingNext.getStart(),
                bookingNext.getEnd(), bookingNext.getItemId(),
                bookingNext.getBookerId(), bookingNext.getStatus(), item.getName());
        ItemDtoWithBooking itemDto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, new ItemDtoWithBooking.Booking(booking.getId(), userBooker.getId()),
                new ItemDtoWithBooking.Booking(bookingNext.getId(), userBooker.getId()), Collections.emptyList());
        PageRequest pageRequest = PageRequest.of(0, 10,
                Sort.by(Sort.Direction.DESC, "start"));
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        bookingsDto.add(bookingDto);
        bookingsDto.add(bookingDtoNext);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking);
        bookingsList.add(bookingNext);
        Page<Booking> bookings = new PageImpl<>(bookingsList);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(bookingRepository.findBookingByBookerId(userBooker.getId(), pageRequest))
                .thenReturn(bookings);
        Mockito
                .when(bookingMapper.toBookingDto(anyIterable(), anyLong()))
                .thenReturn(bookingsDto);
        Collection<BookingDto> result = getBookingService().getUserBookings(userBooker.getId(),
                State.ALL, 0, 10);
        assertEquals(result, bookingsDto);
    }

    @Test
    @SneakyThrows
    void testGetUserBookingsStateCURRENTSuccess() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusWeeks(4);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Booking bookingNext = makeBooking(2L, start.plusWeeks(5), end.plusWeeks(6),
                item.getId(), userBooker.getId(), Status.WAITING);
        BookingDto bookingDto = makeBookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItemId(),
                booking.getBookerId(), booking.getStatus(), item.getName());
        BookingDto bookingDtoNext = makeBookingDto(bookingNext.getId(), bookingNext.getStart(),
                bookingNext.getEnd(), bookingNext.getItemId(),
                bookingNext.getBookerId(), bookingNext.getStatus(), item.getName());
        ItemDtoWithBooking itemDto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, new ItemDtoWithBooking.Booking(booking.getId(), userBooker.getId()),
                new ItemDtoWithBooking.Booking(bookingNext.getId(), userBooker.getId()), Collections.emptyList());
        PageRequest pageRequest = PageRequest.of(0, 10,
                Sort.by(Sort.Direction.DESC, "start"));
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        bookingsDto.add(bookingDto);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking);
        Page<Booking> bookings = new PageImpl<>(bookingsList);
        Clock minuteTickingClock = Clock.tickMinutes(ZoneId.systemDefault());
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(bookingRepository.findBookingByBookerIdAndCurrent(userBooker.getId(),
                        pageRequest))
                .thenReturn(bookings);
        Mockito
                .when(bookingMapper.toBookingDto(anyIterable(), anyLong()))
                .thenReturn(bookingsDto);
        Collection<BookingDto> result = getBookingService().getUserBookings(userBooker.getId(),
                State.CURRENT, 0, 10);
        assertEquals(result.stream().collect(Collectors.toList()).get(0).getId(),
                bookingsDto.stream().collect(Collectors.toList()).get(0).getId());
    }

    @Test
    @SneakyThrows
    void testGetUserBookingsStatePASTSuccess() {
        LocalDateTime start = LocalDateTime.now().minusDays(4);
        LocalDateTime end = LocalDateTime.now().minusDays(2);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Booking bookingNext = makeBooking(2L, start.plusWeeks(5), end.plusWeeks(6),
                item.getId(), userBooker.getId(), Status.WAITING);
        BookingDto bookingDto = makeBookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItemId(),
                booking.getBookerId(), booking.getStatus(), item.getName());
        BookingDto bookingDtoNext = makeBookingDto(bookingNext.getId(), bookingNext.getStart(),
                bookingNext.getEnd(), bookingNext.getItemId(),
                bookingNext.getBookerId(), bookingNext.getStatus(), item.getName());
        ItemDtoWithBooking itemDto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, new ItemDtoWithBooking.Booking(booking.getId(), userBooker.getId()),
                new ItemDtoWithBooking.Booking(bookingNext.getId(), userBooker.getId()), Collections.emptyList());
        PageRequest pageRequest = PageRequest.of(0, 10,
                Sort.by(Sort.Direction.DESC, "start"));
        Clock secondTickingClock = Clock.tickSeconds(ZoneId.systemDefault());
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        bookingsDto.add(bookingDto);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking);
        Page<Booking> bookings = new PageImpl<>(bookingsList);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(bookingRepository.findBookingByBookerIdAndEndIsBefore(userBooker.getId(),
                        LocalDateTime.now(secondTickingClock), pageRequest))
                .thenReturn(bookings);
        Mockito
                .when(bookingMapper.toBookingDto(anyIterable(), anyLong()))
                .thenReturn(bookingsDto);
        Collection<BookingDto> result = getBookingService().getUserBookings(userBooker.getId(),
                State.PAST, 0, 10);
        assertEquals(result.stream().collect(Collectors.toList()).get(0).getId(),
                bookingsDto.stream().collect(Collectors.toList()).get(0).getId());
    }

    @Test
    @SneakyThrows
    void testGetUserBookingsStateFUTURESuccess() {
        LocalDateTime start = LocalDateTime.now().plusDays(4);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User userOwner = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userBooker = makeUser(2L, "Pasha", "pasha@yandex.ru");
        Item item = makeItem(1L, "Отвертка", "Для откручивания",
                true, userOwner.getId());
        Booking booking = makeBooking(1L, start, end, item.getId(),
                userBooker.getId(), Status.WAITING);
        Booking bookingNext = makeBooking(2L, start.plusWeeks(5), end.plusWeeks(6),
                item.getId(), userBooker.getId(), Status.WAITING);
        BookingDto bookingDto = makeBookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItemId(),
                booking.getBookerId(), booking.getStatus(), item.getName());
        BookingDto bookingDtoNext = makeBookingDto(bookingNext.getId(), bookingNext.getStart(),
                bookingNext.getEnd(), bookingNext.getItemId(),
                bookingNext.getBookerId(), bookingNext.getStatus(), item.getName());
        ItemDtoWithBooking itemDto = makeItemDtoWithBooking(1L, "Отвертка", "Для откручивания",
                true, null, new ItemDtoWithBooking.Booking(booking.getId(), userBooker.getId()),
                new ItemDtoWithBooking.Booking(bookingNext.getId(), userBooker.getId()), Collections.emptyList());
        PageRequest pageRequest = PageRequest.of(0, 10,
                Sort.by(Sort.Direction.DESC, "start"));
        Clock secondTickingClock = Clock.tickSeconds(ZoneId.systemDefault());
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        bookingsDto.add(bookingDto);
        bookingsDto.add(bookingDtoNext);
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking);
        bookingsList.add(bookingNext);
        Page<Booking> bookings = new PageImpl<>(bookingsList);
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(userBooker));
        Mockito
                .when(bookingRepository.findBookingByBookerIdAndStartIsAfter(userBooker.getId(),
                        LocalDateTime.now(secondTickingClock), pageRequest))
                .thenReturn(bookings);
        Mockito
                .when(bookingMapper.toBookingDto(anyIterable(), anyLong()))
                .thenReturn(bookingsDto);
        Collection<BookingDto> result = getBookingService().getUserBookings(userBooker.getId(),
                State.FUTURE, 0, 10);
        assertEquals(result.stream().collect(Collectors.toList()).get(0).getId(),
                bookingsDto.stream().collect(Collectors.toList()).get(0).getId());
        assertEquals(result.stream().collect(Collectors.toList()).get(1).getId(),
                bookingsDto.stream().collect(Collectors.toList()).get(1).getId());
    }

    private BookingService getBookingService() {
        return new BookingServiceImpl(bookingRepository, bookingMapper, userService, itemService);
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

    private ItemDtoWithBooking makeItemDtoWithBooking(Long id, String name, String description, Boolean available,
                                                      Long request, ItemDtoWithBooking.Booking lastBooking,
                                                      ItemDtoWithBooking.Booking nextBooking,
                                                      Collection<CommentDtoForItem> comments) {
        ItemDtoWithBooking item = new ItemDtoWithBooking(id, name, description, available, request,
                lastBooking, nextBooking, comments);
        return item;
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
}