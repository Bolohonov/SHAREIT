package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.Item;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Component
public class ItemMapper {
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

    public ItemDtoWithBooking toItemDtoWithBooking(Item item, LocalDateTime lastBookingDate, LocalDateTime nextBookingDate,
                                                   Collection<Comment> comments) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable() != null ? item.getAvailable() : null,
                item.getRequestId(),
                lastBookingDate,
                nextBookingDate,
                comments != null ? comments : Collections.emptyList()
                );
    }
}
