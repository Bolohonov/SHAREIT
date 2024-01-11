package dev.bolohonov.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@ToString
public class CommentDtoForItem {
    /**
     * уникальный идентификатор вещи
     */
    private Long id;
    /**
     * одержимое комментария
     */
    private String text;
    /**
     * имя автора
     */
    private String authorName;
    /**
     * дата создания комментария
     */
    private LocalDateTime created;
}
