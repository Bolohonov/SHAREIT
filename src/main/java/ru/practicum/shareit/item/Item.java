package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDeSerializer;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

/**
 класс с описанием вещи для шеринга - Item
 */
@Data
@Builder
@JsonDeserialize(using = ItemDeSerializer.class)
public class Item {
    /** уникальный идентификатор вещи */
    private Long id;
    /** краткое название */
    @NotBlank
    private String name;
    /** развёрнутое описание */
    @NotBlank
    private String description;
    /** статус о том, доступна или нет вещь для аренды */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean available;
    /** владелец вещи */
    private User owner;
    /**  если вещь была создана по запросу другого пользователя,
     * то в этом поле будет храниться ссылка на соответствующий запрос
     */
    private ItemRequest request;
}
