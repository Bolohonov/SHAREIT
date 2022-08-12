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
    public void searchItemByText() {
        User firstUser = new User();
        ReflectionTestUtils.setField(firstUser, "id", 1L);
        ReflectionTestUtils.setField(firstUser, "name", "Ivan");
        ReflectionTestUtils.setField(firstUser, "email", "ivan@yandex.ru");
        userRepository.save(firstUser);
        User secondUser = new User();
        ReflectionTestUtils.setField(secondUser, "id", 2L);
        ReflectionTestUtils.setField(secondUser, "name", "Pasha");
        ReflectionTestUtils.setField(secondUser, "email", "pasha@yandex.ru");
        userRepository.save(secondUser);

        Item firstItem = new Item();
        ReflectionTestUtils.setField(firstItem, "name", "Отвертка");
        ReflectionTestUtils.setField(firstItem, "description", "Для откручивания");
        ReflectionTestUtils.setField(firstItem, "available", true);
        ReflectionTestUtils.setField(firstItem, "ownerId", 2L);
        itemRepository.save(firstItem);
        Item secondItem = new Item();
        ReflectionTestUtils.setField(secondItem, "name", "Дрель");
        ReflectionTestUtils.setField(secondItem, "description", "Для вкручивания");
        ReflectionTestUtils.setField(secondItem, "available", true);
        ReflectionTestUtils.setField(secondItem, "ownerId", 2L);
        itemRepository.save(secondItem);
        Item thirdItem = new Item();
        ReflectionTestUtils.setField(thirdItem, "name", "Отвертка еще одна");
        ReflectionTestUtils.setField(thirdItem, "description", "Для вкручивания");
        ReflectionTestUtils.setField(thirdItem, "available", false);
        ReflectionTestUtils.setField(thirdItem, "ownerId", 2L);
        itemRepository.save(thirdItem);

        Iterable<Item> foundItems = itemRepository.search(1L, "оТВер", PageRequest.of(0, 10));
        List<Item> pageList = new ArrayList<>();
        foundItems.forEach(pageList::add);
        assertEquals(pageList.size(), 1);
        assertEquals(pageList.get(0), firstItem);
    }
}
