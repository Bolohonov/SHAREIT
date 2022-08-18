package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;

public class CommentDto {
    /**
     * уникальный идентификатор комментария
     */
    private Long id;
    /**
     * одержимое комментария
     */
    @NotBlank
    private String text;
    /**
     * вещь, к которой пользователь оставляет комментарий
     */
    private Long itemId;
    /**
     * автор комментария
     */
    private Long authorId;
}
