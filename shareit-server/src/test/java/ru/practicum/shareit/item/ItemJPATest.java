package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemJPATest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void searchItemByText() {
        User firstUser = makeUser("Ivan", "ivan@yandex.ru");
        userRepository.save(firstUser);
        User secondUser = makeUser("Pasha", "pasha@yandex.ru");
        userRepository.save(secondUser);

        Item firstItem = makeItem("Отвертка", "Для откручивания", true, secondUser.getId());
        itemRepository.save(firstItem);
        Item secondItem = makeItem("Дрель", "Для вкручивания", true, secondUser.getId());
        itemRepository.save(secondItem);
        Item thirdItem = makeItem("Отвертка еще одна", "Для вкручивания",
                false, secondUser.getId());
        itemRepository.save(thirdItem);

        Iterable<Item> foundItems = itemRepository.search(firstUser.getId(), "оТВер",
                PageRequest.of(0, 10));
        List<Item> pageList = new ArrayList<>();
        foundItems.forEach(pageList::add);
        assertEquals(pageList.size(), 1);
        assertEquals(pageList.get(0), firstItem);
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
