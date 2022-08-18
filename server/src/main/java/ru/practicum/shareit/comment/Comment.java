package ru.practicum.shareit.comment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Getter
@Setter
@ToString
public class Comment {
    /**
     * уникальный идентификатор комментария
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * одержимое комментария
     */
    private String text;
    /**
     * вещь, к которой пользователь оставляет комментарий
     */
    @Column(name = "item_id")
    private Long itemId;
    /**
     * автор комментария
     */
    @Column(name = "author_id")
    private Long authorId;
    /**
     * дата создания комментария
     */
    private LocalDateTime created;
}
