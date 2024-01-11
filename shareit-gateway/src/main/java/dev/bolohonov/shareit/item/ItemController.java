package dev.bolohonov.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import dev.bolohonov.shareit.item.dto.CommentDto;
import dev.bolohonov.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Object> saveNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody @Validated ItemDto itemDto) {
        return itemClient.addNewItem(userId, itemDto);
    }

    @PutMapping
    @ResponseStatus(OK)
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Validated ItemDto itemDto) {
        return itemClient.updateItem(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    @ResponseStatus(OK)
    public ResponseEntity<Object> patchedItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        return itemClient.patchedItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(OK)
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long itemId) {
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(OK)
    public ResponseEntity<Object> findAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PositiveOrZero @RequestParam(name = "from",
                                                             defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size",
                                                             defaultValue = "10") Integer size) {
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @NotBlank @RequestParam(value = "text") String text,
                                         @PositiveOrZero @RequestParam(name = "from",
                                                 defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size",
                                                 defaultValue = "10") Integer size) {
        return itemClient.search(userId, text, from, size);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(NO_CONTENT)
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId) {
        return itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(OK)
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Validated CommentDto comment) {
        return itemClient.addComment(userId, itemId, comment);
    }
}
