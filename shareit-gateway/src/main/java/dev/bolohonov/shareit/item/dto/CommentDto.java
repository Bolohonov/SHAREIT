package dev.bolohonov.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
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
