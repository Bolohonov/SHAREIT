package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {
    private final EntityManager em;
    private final BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository;
    private final UserService userservice;
    private final ItemService itemService;

    @Test
    @Transactional
    void approveOrRejectBooking() {
        User user = makeUser("Ivan", "ivan@yandex.ru");
        User userSecond = makeUser("Ivan2", "ivan2@yandex.ru");
        User userThird = makeUser("Ivan3", "ivan3@yandex.ru");
        userservice.saveUser(user);
        userservice.saveUser(userSecond);
        userservice.saveUser(userThird);
        Item firstItem = makeItem("Отвертка", "Для откручивания", true, 1L);
        itemService.addNewItem(user.getId(), firstItem);
        Item secondItem = makeItem("Дрель", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), secondItem);
        Item thirdItem = makeItem("Еще отвертка", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), thirdItem);
        Item forthItem = makeItem("Еще отвертка", "Для вкручивания", true, 1L);
        itemService.addNewItem(user.getId(), forthItem);
        Booking firstBooking = makeBooking(LocalDateTime.of(2023, 8, 10, 10, 10),
                LocalDateTime.of(2023, 8, 11, 10, 10),
                firstItem.getId(), userSecond.getId(), Status.WAITING);
        Booking secondBooking = makeBooking(LocalDateTime.of(2023, 8, 10, 10, 10),
                LocalDateTime.of(2023, 8, 11, 10, 10),
                forthItem.getId(), userSecond.getId(), Status.WAITING);
        bookingService.addNew(userSecond.getId(), firstBooking);
        bookingService.addNew(userSecond.getId(), secondBooking);
        Optional<BookingDto> firstBookingDto = bookingService.approveOrRejectBooking(user.getId(),
                firstBooking.getId(), true);
        Optional<BookingDto> secondBookingDto = bookingService.approveOrRejectBooking(user.getId(),
                secondBooking.getId(), false);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking firstBookingToCompare = query.setParameter("id", firstBooking.getId()).getSingleResult();
        Booking secondBookingToCompare = query.setParameter("id", secondBooking.getId()).getSingleResult();
        assertEquals(firstBooking.getStatus(), firstBookingToCompare.getStatus());
        assertEquals(secondBooking.getStatus(), secondBookingToCompare.getStatus());
        assertEquals(firstBooking.getStatus(), Status.APPROVED);
        assertEquals(secondBooking.getStatus(), Status.REJECTED);
    }

    @Test
    @Transactional
    void getBookingsByOwnerStateCURRENT() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User user = makeUser("Ivan", "ivan@yandex.ru");
        User userSecond = makeUser("Ivan2", "ivan2@yandex.ru");
        User userThird = makeUser("Ivan3", "ivan3@yandex.ru");
        userservice.saveUser(user);
        userservice.saveUser(userSecond);
        userservice.saveUser(userThird);
        Item firstItem = makeItem("Отвертка", "Для откручивания", true, 1L);
        itemService.addNewItem(user.getId(), firstItem);
        Item secondItem = makeItem("Дрель", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), secondItem);
        Item thirdItem = makeItem("Еще отвертка", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), thirdItem);
        Item forthItem = makeItem("Еще отвертка", "Для вкручивания", true, 1L);
        itemService.addNewItem(user.getId(), forthItem);
        Booking firstBooking = makeBooking(start, end, firstItem.getId(), userSecond.getId(), Status.WAITING);
        Booking secondBooking = makeBooking(start, end, forthItem.getId(), userSecond.getId(), Status.WAITING);
        bookingService.addNew(userSecond.getId(), firstBooking);
        bookingService.addNew(userSecond.getId(), secondBooking);
        BookingDto firstBookingDto = makeBookingDto(firstBooking.getId(), start, end,
                firstItem.getId(), userSecond.getId(), Status.WAITING, firstItem.getName());
        BookingDto secondBookingDto = makeBookingDto(secondBooking.getId(), start, end, secondItem.getId(),
                userSecond.getId(), Status.WAITING, secondItem.getName());
        Collection<BookingDto> dtos = new ArrayList<>();
        dtos.add(firstBookingDto);
        dtos.add(secondBookingDto);
        Collection<BookingDto> result = bookingService.getBookingsByOwner(user.getId(), State.CURRENT, 0, 10);
        assertEquals(dtos.stream().collect(Collectors.toList()).get(0).getId(),
                result.stream().collect(Collectors.toList()).get(0).getId());
        assertEquals(dtos.stream().collect(Collectors.toList()).get(1).getId(),
                result.stream().collect(Collectors.toList()).get(1).getId());
    }

    @Test
    @Transactional
    void getBookingsByOwnerStatePAST() {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        User user = makeUser("Ivan", "ivan@yandex.ru");
        User userSecond = makeUser("Ivan2", "ivan2@yandex.ru");
        User userThird = makeUser("Ivan3", "ivan3@yandex.ru");
        userservice.saveUser(user);
        userservice.saveUser(userSecond);
        userservice.saveUser(userThird);
        Item firstItem = makeItem("Отвертка", "Для откручивания", true, 1L);
        itemService.addNewItem(user.getId(), firstItem);
        Item secondItem = makeItem("Дрель", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), secondItem);
        Item thirdItem = makeItem("Еще отвертка", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), thirdItem);
        Item forthItem = makeItem("Еще отвертка", "Для вкручивания", true, 1L);
        itemService.addNewItem(user.getId(), forthItem);
        Booking firstBooking = makeBooking(start, end, firstItem.getId(), userSecond.getId(), Status.WAITING);
        Booking secondBooking = makeBooking(start, end, forthItem.getId(), userSecond.getId(), Status.WAITING);
        bookingRepository.save(firstBooking);
        bookingRepository.save(secondBooking);
        BookingDto firstBookingDto = makeBookingDto(firstBooking.getId(), start, end,
                firstItem.getId(), userSecond.getId(), Status.WAITING, firstItem.getName());
        BookingDto secondBookingDto = makeBookingDto(secondBooking.getId(), start, end, secondItem.getId(),
                userSecond.getId(), Status.WAITING, secondItem.getName());
        Collection<BookingDto> dtos = new ArrayList<>();
        dtos.add(firstBookingDto);
        dtos.add(secondBookingDto);
        Collection<BookingDto> result = bookingService.getBookingsByOwner(user.getId(), State.PAST, 0, 10);
        assertEquals(dtos.stream().collect(Collectors.toList()).get(0).getId(),
                result.stream().collect(Collectors.toList()).get(0).getId());
        assertEquals(dtos.stream().collect(Collectors.toList()).get(1).getId(),
                result.stream().collect(Collectors.toList()).get(1).getId());
    }

    @Test
    @Transactional
    void getBookingsByOwnerStateFUTURE() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        User user = makeUser("Ivan", "ivan@yandex.ru");
        User userSecond = makeUser("Ivan2", "ivan2@yandex.ru");
        User userThird = makeUser("Ivan3", "ivan3@yandex.ru");
        userservice.saveUser(user);
        userservice.saveUser(userSecond);
        userservice.saveUser(userThird);
        Item firstItem = makeItem("Отвертка", "Для откручивания", true, 1L);
        itemService.addNewItem(user.getId(), firstItem);
        Item secondItem = makeItem("Дрель", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), secondItem);
        Item thirdItem = makeItem("Еще отвертка", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), thirdItem);
        Item forthItem = makeItem("Еще отвертка", "Для вкручивания", true, 1L);
        itemService.addNewItem(user.getId(), forthItem);
        Booking firstBooking = makeBooking(start, end, firstItem.getId(), userSecond.getId(), Status.WAITING);
        Booking secondBooking = makeBooking(start, end, forthItem.getId(), userSecond.getId(), Status.WAITING);
        bookingRepository.save(firstBooking);
        bookingRepository.save(secondBooking);
        BookingDto firstBookingDto = makeBookingDto(firstBooking.getId(), start, end,
                firstItem.getId(), userSecond.getId(), Status.WAITING, firstItem.getName());
        BookingDto secondBookingDto = makeBookingDto(secondBooking.getId(), start, end, secondItem.getId(),
                userSecond.getId(), Status.WAITING, secondItem.getName());
        Collection<BookingDto> dtos = new ArrayList<>();
        dtos.add(firstBookingDto);
        dtos.add(secondBookingDto);
        Collection<BookingDto> result = bookingService.getBookingsByOwner(user.getId(), State.FUTURE, 0, 10);
        assertEquals(dtos.stream().collect(Collectors.toList()).get(0).getId(),
                result.stream().collect(Collectors.toList()).get(0).getId());
        assertEquals(dtos.stream().collect(Collectors.toList()).get(1).getId(),
                result.stream().collect(Collectors.toList()).get(1).getId());
    }
    @Test
    @Transactional
    void getBookingsByOwnerStateALL() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        User user = makeUser("Ivan", "ivan@yandex.ru");
        User userSecond = makeUser("Ivan2", "ivan2@yandex.ru");
        User userThird = makeUser("Ivan3", "ivan3@yandex.ru");
        userservice.saveUser(user);
        userservice.saveUser(userSecond);
        userservice.saveUser(userThird);
        Item firstItem = makeItem("Отвертка", "Для откручивания", true, 1L);
        itemService.addNewItem(user.getId(), firstItem);
        Item secondItem = makeItem("Дрель", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), secondItem);
        Item thirdItem = makeItem("Еще отвертка", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), thirdItem);
        Item forthItem = makeItem("Еще отвертка", "Для вкручивания", true, 1L);
        itemService.addNewItem(user.getId(), forthItem);
        Booking firstBooking = makeBooking(start, end, firstItem.getId(), userSecond.getId(), Status.WAITING);
        Booking secondBooking = makeBooking(start, end, forthItem.getId(), userSecond.getId(), Status.WAITING);
        bookingService.addNew(userSecond.getId(), firstBooking);
        bookingService.addNew(userSecond.getId(), secondBooking);
        BookingDto firstBookingDto = makeBookingDto(firstBooking.getId(), start, end,
                firstItem.getId(), userSecond.getId(), Status.WAITING, firstItem.getName());
        BookingDto secondBookingDto = makeBookingDto(secondBooking.getId(), start, end, secondItem.getId(),
                userSecond.getId(), Status.WAITING, secondItem.getName());
        Collection<BookingDto> dtos = new ArrayList<>();
        dtos.add(firstBookingDto);
        dtos.add(secondBookingDto);
        Collection<BookingDto> result = bookingService.getBookingsByOwner(user.getId(), State.ALL, 0, 10);
        assertEquals(dtos.stream().collect(Collectors.toList()).get(0).getId(),
                result.stream().collect(Collectors.toList()).get(0).getId());
        assertEquals(dtos.stream().collect(Collectors.toList()).get(1).getId(),
                result.stream().collect(Collectors.toList()).get(1).getId());


    }

    private Booking makeBooking(LocalDateTime start, LocalDateTime end, Long itemId, Long bookerId,
                                Status status) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItemId(itemId);
        booking.setBookerId(bookerId);
        booking.setStatus(status);
        return booking;
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item makeItem(String name, String description, Boolean available, Long ownerId) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        return item;
    }

    private BookingDto makeBookingDto(Long id, LocalDateTime start, LocalDateTime end, Long itemId, Long bookerId,
                                      Status status, String itemName) {
        BookingDto booking = new BookingDto(id, start, end, new BookingDto.Item(itemId, itemName),
                new BookingDto.Booker(bookerId), status);
        return booking;
    }
}