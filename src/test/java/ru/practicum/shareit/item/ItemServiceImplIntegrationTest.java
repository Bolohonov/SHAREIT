package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        User userSecond = makeUser( "Ivan2", "ivan2@yandex.ru");
        userservice.saveUser(user);
        userservice.saveUser(userSecond);
        Item firstItem = makeItem("Отвертка", "Для откручивания", true, user.getId());
        itemService.addNewItem(user.getId(), firstItem);
        Item secondItem = makeItem("Дрель", "Для вкручивания", true,
                userSecond.getId());
        itemService.addNewItem(userSecond.getId(), secondItem);
        Item thirdItem = makeItem("Еще отвертка", "Для вкручивания", true,
                userSecond.getId());
        itemService.addNewItem(userSecond.getId(), thirdItem);
        Item forthItem = makeItem("Еще отвертка", "Для вкручивания", true, user.getId());
        itemService.addNewItem(user.getId(), forthItem);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.ownerId = :ownerId", Item.class);
        Collection<ItemDtoWithBooking> userItems = itemService.getAllUserItems(user.getId());
        Collection<Item> userItemsToCompare = query.setParameter("ownerId", user.getId()).getResultList();
        List<ItemDtoWithBooking> userItemsList = userItems.stream()
                .sorted(Comparator.comparingLong(i -> i.getId()))
                .collect(Collectors.toList());
        assertEquals(userItems.size(), userItemsToCompare.size());
        assertEquals(userItems.size(), 2);
        assertEquals(userItemsList.get(0).getName(), "Отвертка");
        assertEquals(userItemsList.get(1).getName(), "Еще отвертка");
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
}