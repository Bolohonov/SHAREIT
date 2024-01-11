package dev.bolohonov.shareit.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * класс, отвечающий за запрос вещи //
 */
@Entity
@Indexed
@Table(name = "item_requests", schema = "public")
@Getter
@Setter
@ToString
public class ItemRequest {
    /**
     * уникальный идентификатор запроса
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * текст запроса, содержащий описание требуемой вещи
     */
    @Column(name = "description")
    private String description;
    /**
     * пользователь, создавший запрос
     */
    @Column(name = "requester_id")
    private Long requesterId;
    /**
     * дата и время создания запроса
     */
    @Column(name = "created")
    private LocalDateTime created;
}
