package ru.practicum.shareit.item;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 класс с описанием вещи для шеринга - Item
 */
@Entity
@Table(name = "items", schema = "public")
@Getter @Setter @ToString
public class Item {
    /** уникальный идентификатор вещи */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** краткое название */
    @NotBlank
    private String name;
    /** развёрнутое описание */
    @NotBlank
    private String description;
    /** статус о том, доступна или нет вещь для аренды */
    @NotNull
    private Boolean available;
    /** владелец вещи */
    @Column(name = "owner_id")
    private Long ownerId;
    /**  если вещь была создана по запросу другого пользователя,
     * то в этом поле будет храниться ссылка на соответствующий запрос
     */
    @Column(name = "request_id")
    private Long requestId;
}
