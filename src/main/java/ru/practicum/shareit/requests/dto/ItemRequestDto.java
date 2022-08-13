package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    /**
     * уникальный идентификатор запроса
     */
    private Long id;
    /**
     * уникальный идентификатор запроса
     */
    private String description;
    /**
     * дата и время создания запроса
     */
    private LocalDateTime created;
}
