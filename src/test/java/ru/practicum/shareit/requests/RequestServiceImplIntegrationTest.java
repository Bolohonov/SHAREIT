package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplIntegrationTest {
    private final EntityManager em;
    private final UserService userservice;
    private final ItemService itemService;
    private final RequestServiceImpl requestService;

    @Test
    void addNewRequest() {
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

        ItemRequest itemRequest = makeItemRequest("Нужен шуруповерт", user.getId(),
                LocalDateTime.of(2022, 8, 11, 10, 10));
        requestService.addNewRequest(user.getId(), itemRequest);
        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.id = :id",
                ItemRequest.class);
        ItemRequest itemRequestToCompare = query.setParameter("id", itemRequest.getId()).getSingleResult();
        assertEquals(itemRequest, itemRequestToCompare);
        assertEquals(itemRequestToCompare.getDescription(), "Нужен шуруповерт");
        assertEquals(itemRequestToCompare.getRequesterId(), user.getId());
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

    private ItemRequest makeItemRequest(String description, Long requesterId, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(description);
        itemRequest.setRequesterId(requesterId);
        itemRequest.setCreated(created);
        return itemRequest;
    }
}