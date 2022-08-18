package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;

public class ItemRequestDto {
    /**
     * уникальный идентификатор запроса
     */
    private Long id;
    /**
     * текст запроса, содержащий описание требуемой вещи
     */
    @NotBlank
    private String description;
    /**
     * пользователь, создавший запрос
     */
    private Long requesterId;
}
