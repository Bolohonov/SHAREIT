package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {
    /** уникальный идентификатор вещи */
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
    private Long request;

    public ItemDto(Long id, String name, String description, boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
