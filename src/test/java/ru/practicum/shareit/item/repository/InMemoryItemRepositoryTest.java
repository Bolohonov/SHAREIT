package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class InMemoryItemRepositoryTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void searchItems() {
        User user = User.builder()
                .id(1L)
                .name("testNameForSearch")
                .email("test@yandex.ru")
                .build();
        userRepository.addUser(user);
        Item item = Item.builder()
                .id(1L)
                .name("testNameForSearch")
                .owner(user)
                .description("ForSearchTest")
                .available(true)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("testName")
                .owner(user)
                .description("ForTest")
                .available(true)
                .build();
        Item item3 = Item.builder()
                .id(3L)
                .name("testName")
                .owner(user)
                .description("ForSearchTest")
                .available(true)
                .build();
        itemRepository.addItem(1L, item);
        itemRepository.addItem(1L, item2);
        itemRepository.addItem(1L, item3);
        List<Item> list = itemRepository.searchItems(1L,"search").stream().collect(Collectors.toList());
        assertThat(list.size() == 2);
        assertThat(list.get(0) == item3);
        assertThat(list.get(1) == item);
    }
}