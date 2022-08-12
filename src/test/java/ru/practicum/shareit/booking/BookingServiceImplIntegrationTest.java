package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {
    private final EntityManager em;
    private final BookingServiceImpl bookingService;
    private final UserService userservice;
    private final ItemService itemService;

    @Test
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

    private Booking makeBooking(LocalDateTime start, LocalDateTime end, Long itemId, Long bookerId,
                                Status status) {
        Booking booking = new Booking();
        ReflectionTestUtils.setField(booking, "start", start);
        ReflectionTestUtils.setField(booking, "end", end);
        ReflectionTestUtils.setField(booking, "itemId", itemId);
        ReflectionTestUtils.setField(booking, "bookerId", bookerId);
        ReflectionTestUtils.setField(booking, "status", status);
        return booking;
    }

    private User makeUser(String name, String email) {
        User user = new User();
        ReflectionTestUtils.setField(user, "name", name);
        ReflectionTestUtils.setField(user, "email", email);
        return user;
    }

    private Item makeItem(String name, String description, Boolean available, Long ownerId) {
        Item item = new Item();
        ReflectionTestUtils.setField(item, "name", name);
        ReflectionTestUtils.setField(item, "description", description);
        ReflectionTestUtils.setField(item, "available", available);
        ReflectionTestUtils.setField(item, "ownerId", ownerId);
        return item;
    }
}