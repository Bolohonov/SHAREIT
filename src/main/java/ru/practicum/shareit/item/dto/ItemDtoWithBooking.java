package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.comment.dto.CommentDtoForItem;

import java.io.Serializable;
import java.util.Collection;

@Data
@AllArgsConstructor
public class ItemDtoWithBooking {
    /**
     * уникальный идентификатор вещи
     */
    private Long id;
    /**
     * краткое название
     */
    private String name;
    /**
     * развёрнутое описание
     */
    private String description;
    /**
     * статус о том, доступна или нет вещь для аренды
     */
    private Boolean available;
    /**
     * если вещь была создана по запросу другого пользователя,
     * то в этом поле будет храниться ссылка на соответствующий запрос
     */
    private Long request;
    /**
     * дата окончания последнего бронирования
     */
    private Booking lastBooking;
    /**
     * дата начала следующего бронирования
     */
    private Booking nextBooking;
    /**
     * комментарии по использования вещи
     */
    private Collection<CommentDtoForItem> comments;

    @AllArgsConstructor
    @ToString
    @Getter
    @Setter
    static class Booking implements Serializable {
        private Long id;
        private Long bookerId;
    }
}
