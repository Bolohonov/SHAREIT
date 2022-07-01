package ru.practicum.shareit.requests.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
public class ItemRequestDto {
    /** уникальный идентификатор запроса */
    private Long id;
    /** текст запроса, содержащий описание требуемой вещи */
    private String description;
    /** дата и время создания запроса */
    private LocalDateTime created;

    public ItemRequestDto(String description, LocalDateTime created) {
        this.description = description;
        this.created = created;
    }
}
