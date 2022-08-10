package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collection;

@AllArgsConstructor
public class ItemRequestDtoWithResponses {
    /**
     * уникальный идентификатор запроса
     */
    private String description;
    /**
     * дата и время создания запроса
     */
    private LocalDateTime created;

    private Collection<Response> responses;

    @AllArgsConstructor
    @ToString
    @Getter
    @Setter
    static class Response {
        /**
         * уникальный идентификатор вещи
         */
        private Long id;
        /**
         * краткое название
         */
        private String name;
        /**
         * развёрнутое описание
         */
        private String description;
    }
}

