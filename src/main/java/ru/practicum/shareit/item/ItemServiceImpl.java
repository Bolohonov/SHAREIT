package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.AccessToItemException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.of;

@Service
@RequiredArgsConstructor
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
        if (oldItem.getId() != item.getId()) {
            throw new AccessToItemException("Доступ запрещен!");
        }
        return of(itemMapper.toItemDto(itemRepository.updateItem(userId, item)));
    }

    @Override
    public Optional<ItemDto> findItemById(Long itemId){
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
    public Collection<ItemDto> search(String text) {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : itemRepository.searchItems(text)) {
            itemsDto.add(itemMapper.toItemDto(i));
        }
        return itemsDto;
    }

    @Override
    public boolean deleteItem(Long userId, Long itemId) {
        if (itemRepository.findItemById(itemId).isEmpty()
                || itemRepository.findItemById(itemId).get().getOwner().getId() != userId) {
            return false;
        } else {
            itemRepository.deleteItem(userId, itemId);
            return true;
        }
    }
}
