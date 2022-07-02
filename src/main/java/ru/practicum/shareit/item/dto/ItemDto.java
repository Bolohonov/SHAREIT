package ru.practicum.shareit.item.dto;

import lombok.Data;

/**
 * // TODO .
 */
@Data
public class ItemDto {
    /** краткое название */
    private String name;
    /** развёрнутое описание */
    private String description;
    /** статус о том, доступна или нет вещь для аренды */
    private boolean available;
    /**  если вещь была создана по запросу другого пользователя,
     * то в этом поле будет храниться ссылка на соответствующий запрос
     */
    private Long request;

    public ItemDto(String name, String description, boolean available, Long request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
