package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.Collection;
import java.util.Optional;

public interface ItemService {
    ItemDto addNewItem(Long userId, Item item);

    Optional<ItemDto> updateItem(Long userId, Item item);

    Optional<ItemDto> patchedItem(Long userId, Long itemId, String item);

    Optional<ItemDtoWithBooking> findItemById(Long itemId, Long userId);

    Collection<ItemDtoWithBooking> getUserItems(Long userId);

    boolean deleteItem(Long userId, Long itemId);

    boolean checkOwner(Long userId, Long itemId);

    Collection<ItemDto> search(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, Comment comment);
}
