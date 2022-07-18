package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDate;

@AllArgsConstructor
@Data
public class CommentDto {
    /** одержимое комментария */
    private String text;
    /** вещь, к которой пользователь оставляет комментарий */
    @Column(name = "item_id")
    private Long itemId;
    /** автор комментария */
    @Column(name = "author_id")
    private Long authorId;
    /** дата создания комментария */
    private LocalDate created;
}
