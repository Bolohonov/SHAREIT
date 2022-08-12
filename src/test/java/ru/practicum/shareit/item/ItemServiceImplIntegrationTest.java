package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userservice;

    @Test
    void getAllUserItems() {
        User user = makeUser("Ivan", "ivan@yandex.ru");
        User userSecond = makeUser("Ivan2", "ivan2@yandex.ru");
        userservice.saveUser(user);
        userservice.saveUser(userSecond);
        Item firstItem = makeItem("Отвертка", "Для откручивания", true, 1L);
        itemService.addNewItem(user.getId(), firstItem);
        Item secondItem = makeItem("Дрель", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), secondItem);
        Item thirdItem = makeItem("Еще отвертка", "Для вкручивания", true, 2L);
        itemService.addNewItem(userSecond.getId(), thirdItem);
        Item forthItem = makeItem("Еще отвертка", "Для вкручивания", true, 1L);
        itemService.addNewItem(user.getId(), forthItem);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.ownerId = :ownerId", Item.class);
        Collection<ItemDtoWithBooking> userItems = itemService.getAllUserItems(firstItem.getId());
        Collection<Item> userItemsToCompare = query.setParameter("ownerId", firstItem.getOwnerId()).getResultList();

        List<ItemDtoWithBooking> userItemsList = userItems.stream()
                .sorted(Comparator.comparingLong(i -> i.getId()))
                .collect(Collectors.toList());

        assertEquals(userItems.size(), userItemsToCompare.size());
        assertEquals(userItems.size(), 2);
        assertEquals(userItemsList.get(0).getName(), "Отвертка");
        assertEquals(userItemsList.get(1).getName(), "Еще отвертка");
    }

    private Item makeItem(String name, String description, Boolean available, Long ownerId) {
        Item item = new Item();
        ReflectionTestUtils.setField(item, "name", name);
        ReflectionTestUtils.setField(item, "description", description);
        ReflectionTestUtils.setField(item, "available", available);
        ReflectionTestUtils.setField(item, "ownerId", ownerId);
        return item;
    }

    private User makeUser(String name, String email) {
        User user = new User();
        ReflectionTestUtils.setField(user, "name", name);
        ReflectionTestUtils.setField(user, "email", email);
        return user;
    }
}