package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * // TODO .
 */
public class ItemDto {
    /** уникальный идентификатор вещи */
    private Long id;
    /** краткое название */
    private String name;
    /** развёрнутое описание */
    private String description;
    /** статус о том, доступна или нет вещь для аренды */
    private boolean available;
    /**  если вещь была создана по запросу другого пользователя,
     * то в этом поле будет храниться ссылка на соответствующий запрос
     */
    private ItemRequest request;

    public ItemDto(String name, String description, boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = new ItemRequest(requestId);
    }

    static class ItemRequest {
        /** уникальный идентификатор запроса */
        private Long id;
        /** текст запроса, содержащий описание требуемой вещи */
        private String description;

        public ItemRequest(Long requestId) {
            this.id = requestId;
        }
    }
}
