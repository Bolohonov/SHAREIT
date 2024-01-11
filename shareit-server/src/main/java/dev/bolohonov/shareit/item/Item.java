package dev.bolohonov.shareit.item;

import lombok.*;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;

/**
 * класс с описанием вещи для шеринга - Item
 */
@Entity
@Indexed
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
public class Item {
    /**
     * уникальный идентификатор вещи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * владелец вещи
     */
    @Column(name = "owner_id")
    private Long ownerId;
    /**
     * если вещь была создана по запросу другого пользователя,
     * то в этом поле будет храниться ссылка на соответствующий запрос
     */
    @Column(name = "request_id")
    private Long requestId;
}
