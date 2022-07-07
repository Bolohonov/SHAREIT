package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item addItem(Long userId, Item item);

    Item updateItem(Long id, Item item);

    Optional<Item> findItemById(Long id);

    Collection<Item> findUserItems(Long userId);

    void deleteItem(Long userId, Long itemId);

    Collection<Item> searchItems(Long userId, String text);
}
