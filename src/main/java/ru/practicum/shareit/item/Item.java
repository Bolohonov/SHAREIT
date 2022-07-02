package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

/**
 класс с описанием вещи для шеринга - Item
 */
@Data
@Builder
public class Item {
    /** уникальный идентификатор вещи */
    private Long id;
    /** краткое название */
    @NotBlank
    private String name;
    /** развёрнутое описание */
    private String description;
    /** статус о том, доступна или нет вещь для аренды */
    private boolean available;
    /** владелец вещи */
    private User owner;
    /**  если вещь была создана по запросу другого пользователя,
     * то в этом поле будет храниться ссылка на соответствующий запрос
     */
    private ItemRequest request;
}
