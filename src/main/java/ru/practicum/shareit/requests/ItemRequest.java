package ru.practicum.shareit.requests;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * класс, отвечающий за запрос вещи //
 */
@Data
public class ItemRequest {
    /**
     * уникальный идентификатор запроса
     */
    private Long id;
    /**
     * текст запроса, содержащий описание требуемой вещи
     */
    private String description;
    /**
     * пользователь, создавший запрос
     */
    private User requestor;
    /**
     * дата и время создания запроса
     */
    private LocalDateTime created;
}
