package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Optional;

public interface ItemService {
    ItemDto addNewItem(Long userId, Item item);

    Optional<ItemDto> updateItem(Long userId, Item item);

    Optional<ItemDto> findItemById(Long itemId);

    Collection<ItemDto> getUserItems(Long userId);

    boolean deleteItem(Long userId, Long itemId);

    Collection<ItemDto> search(String text);
}
