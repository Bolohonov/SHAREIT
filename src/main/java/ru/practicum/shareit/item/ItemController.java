package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import javax.validation.Valid;
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
                               @Valid @RequestBody Item item) {
        return itemService.addNewItem(userId, item);
    }

    @PutMapping
    @ResponseStatus(OK)
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody Item item) {
        return itemService.updateItem(userId, item).orElseThrow(() -> {
            log.warn("пользователь с id {} не найден для обновления", userId);
            throw new ResponseStatusException(BAD_REQUEST);
        });
    }

    @PatchMapping("{id}")
    @ResponseStatus(OK)
    public ItemDto patchedItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestBody String json) {
        return itemService.patchedItem(userId, id, json).orElseThrow(() -> {
            log.warn("пользователь с id {} не найден для обновления", userId);
            throw new ResponseStatusException(BAD_REQUEST);
        });
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public ItemDto findItemById(@PathVariable Long id) {
        return itemService.findItemById(id)
                .orElseThrow(() -> {
                    log.warn("предмет с id {} не найден", id);
                    throw new ResponseStatusException(NOT_FOUND);
                });
    }

    @GetMapping
    @ResponseStatus(OK)
    public Collection<ItemDtoWithBooking> findAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(value = "text") String text) {
        return itemService.search(userId, text);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long id) {
        if (!itemService.deleteItem(userId, id)) {
            log.warn("режиссер с id {} не найден для удаления", id);
            throw new ResponseStatusException(BAD_REQUEST);
        }
    }
}
