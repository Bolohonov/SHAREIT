package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.Collection;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(CREATED)
    public ItemDto saveNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestBody Item item) {
        return itemService.addNewItem(userId, item);
    }

    @PutMapping
    @ResponseStatus(OK)
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody Item item) {
        return itemService.updateItem(userId, item).get();
    }

    @PatchMapping("{id}")
    @ResponseStatus(OK)
    public ItemDto patchedItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestBody String json) {
        return itemService.patchedItem(userId, id, json).get();
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public ItemDtoWithBooking findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long id) {
        return itemService.findItemById(id, userId).get();
    }

    @GetMapping
    @ResponseStatus(OK)
    public Collection<ItemDtoWithBooking> findAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam(value = "from", defaultValue =
                                                                     "0") Integer from,
                                                             @RequestParam(value = "size", defaultValue =
                                                                     "50") Integer size) {
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(value = "text") String text,
                                      @RequestParam(value = "from", defaultValue =
                                              "0") Integer from,
                                      @RequestParam(value = "size", defaultValue =
                                              "50") Integer size) {
        return itemService.search(userId, text, from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long id) {
        itemService.deleteItem(userId, id);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(OK)
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody Comment comment,
                                    @PathVariable Long itemId) {
        return itemService.addComment(userId, itemId, comment);
    }
}
