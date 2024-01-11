package dev.bolohonov.shareit.requests.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@AllArgsConstructor
@Builder
@Data
public class ItemRequestDtoWithResponses {
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

    private Collection<Response> items;

    @AllArgsConstructor
    @Builder
    @ToString
    @Getter
    @Setter
    public static class Response implements Serializable {
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
        /**
         * статус о том, доступна или нет вещь для аренды
         */
        private Boolean available;
        /**
         * если вещь была создана по запросу другого пользователя,
         * то в этом поле будет храниться ссылка на соответствующий запрос
         */
        private Long requestId;
    }
}

