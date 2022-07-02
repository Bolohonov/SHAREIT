package ru.practicum.shareit.requests.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    /** уникальный идентификатор запроса */
    private String description;
    /** дата и время создания запроса */
    private LocalDateTime created;

    public ItemRequestDto(String description, LocalDateTime created) {
        this.description = description;
        this.created = created;
    }
}
