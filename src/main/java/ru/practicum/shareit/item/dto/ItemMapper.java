package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDtoForItem;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentMapper commentMapper;
    private final UserService userService;

    public static ItemDto toItemDto(Item item, Collection<Comment> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable() != null ? item.getAvailable() : null,
                item.getRequestId(),
                comments != null ? comments : Collections.emptyList()
        );
    }

    public static Collection<ItemDtoWithoutComments> toItemDtoWithoutComments(Collection<Item> items) {
        Collection<ItemDtoWithoutComments> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(new ItemDtoWithoutComments(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable() != null ? item.getAvailable() : null,
                    item.getRequestId())
            );
        }
        return dtos;
    }

    public ItemDtoWithBooking toItemDtoWithBooking(Item item, Optional<Booking> last, Optional<Booking> next,
                                                   Collection<Comment> comments) {
        Collection<CommentDtoForItem> commentsForItem = new ArrayList<>();
        if (comments != null) {
            for (Comment com : comments) {
                commentsForItem.add(commentMapper.toCommentDtoForItem(com,
                        userService.getUserById(com.getAuthorId()).get().getName()));
            }
        }
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable() != null ? item.getAvailable() : null,
                item.getRequestId(),
                last.isPresent() ? new ItemDtoWithBooking.Booking(last.get().getId(), last.get().getBookerId()) : null,
                next.isPresent() ? new ItemDtoWithBooking.Booking(next.get().getId(), next.get().getBookerId()) : null,
                commentsForItem != null ? commentsForItem : Collections.emptyList()
        );
    }
}
