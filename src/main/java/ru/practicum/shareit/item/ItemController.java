package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import javax.validation.Valid;
import java.util.Collection;

import static org.springframework.http.HttpStatus.*;

/**
 * // TODO .
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(CREATED)
    public ItemDto saveNewItem(@RequestHeader("X-Later-User-Id") Long userId,
                            @Valid @RequestBody Item item) {
        return itemService.addNewItem(userId, item);
    }

    @PutMapping
    @ResponseStatus(OK)
    public ItemDto updateItem(@RequestHeader("X-Later-User-Id") Long userId,
                           @Valid @RequestBody Item item) {
        if (userId != item.getOwner().getId()) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        return itemService.updateItem(userId, item).orElseThrow(() -> {
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
    public Collection<ItemDto> findAllItemsOfUser(@RequestHeader("X-Later-User-Id") Long userId) {
        return itemService.getUserItems(userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteItem(@RequestHeader("X-Later-User-Id") Long userId,
                           @PathVariable Long id) {
        if (!itemService.deleteItem(userId,id)) {
            log.warn("режиссер с id {} не найден для удаления", id);
            throw new ResponseStatusException(BAD_REQUEST);
        }
    }
}
