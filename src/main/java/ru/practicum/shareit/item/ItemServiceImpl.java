package ru.practicum.shareit.item;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.AccessToItemException;
import ru.practicum.shareit.item.exceptions.ItemValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    public ItemDto addNewItem(Long userId, Item item) {
        item.setOwner(userService.getUserById(userId).get());
        return itemMapper.toItemDto(itemRepository.addItem(userId, item));
    }

    @Override
    public Optional<ItemDto> updateItem(Long userId, Item item) {
        Item oldItem = itemRepository.findItemById(item.getId()).get();
        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new AccessToItemException("Доступ запрещен!");
        }
        item.setOwner(userService.getUserById(userId).get());
        return of(itemMapper.toItemDto(itemRepository.updateItem(userId, item)));
    }

    @Override
    public Optional<ItemDto> patchedItem(Long userId, Long itemId, String json) {
        Item item = itemRepository.findItemById(itemId).get();
        JsonObject obj = new Gson().fromJson(json, JsonObject.class);
        Optional<String> name;
        Optional<String> description;
        Optional<Boolean> available;
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessToItemException("Доступ запрещен!");
        }
        try {
            name = ofNullable(obj.get("name").getAsString());
            item.setName(name.get());
        } catch (NullPointerException e) {
            log.info("Часть полей полученного объекта пустые");
        }
        try {
            description = ofNullable(obj.get("description").getAsString());
            item.setDescription(description.get());
        } catch (NullPointerException e) {
            log.info("Часть полей полученного объекта пустые");
        }
        try {
            available = ofNullable(obj.get("available").getAsBoolean());
            item.setAvailable(available.get());
        } catch (NullPointerException e) {
            log.info("Часть полей полученного объекта пустые");
        }
        return of(itemMapper.toItemDto(itemRepository.updateItem(userId, item)));
    }

    @Override
    public Optional<ItemDto> findItemById(Long itemId) {
        return of(itemMapper.toItemDto(itemRepository.findItemById(itemId).orElseThrow()));
    }

    @Override
    public Collection<ItemDto> getUserItems(Long userId) {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : itemRepository.findUserItems(userId)) {
            itemsDto.add(itemMapper.toItemDto(i));
        }
        return itemsDto;
    }

    @Override
    public Collection<ItemDto> search(Long userId, String text) {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        if (!text.isEmpty()) {
            for (Item i : itemRepository.searchItems(userId, text)) {
                itemsDto.add(itemMapper.toItemDto(i));
            }
        } else {
            return Collections.emptyList();
        }
        return itemsDto;
    }

    @Override
    public boolean deleteItem(Long userId, Long itemId) {
        if (itemRepository.findItemById(itemId).isEmpty()
                || itemRepository.findItemById(itemId).get().getOwner().getId().equals(userId)) {
            return false;
        } else {
            itemRepository.deleteItem(userId, itemId);
            return true;
        }
    }
}
