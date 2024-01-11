package dev.bolohonov.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentDto {
    /**
     * уникальный идентификатор вещи
     */
    private Long id;
    /**
     * одержимое комментария
     */
    private String text;
    /**
     * вещь, к которой пользователь оставляет комментарий
     */
    private Long itemId;
    /**
     * автор комментария
     */
    private Long authorId;
    /**
     * дата создания комментария
     */
    private LocalDateTime created;
    private String authorName;
}
