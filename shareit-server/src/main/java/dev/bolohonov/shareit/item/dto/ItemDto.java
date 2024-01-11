package dev.bolohonov.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import dev.bolohonov.shareit.comment.Comment;

import java.util.Collection;

@Data
@AllArgsConstructor
public class ItemDto {
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
    private Long requestId;

    /**
     * комментарии по использования вещи
     */
    private Collection<Comment> comments;
}
