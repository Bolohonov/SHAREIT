package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;

/**
 класс с описанием бронирования вещи - Booking //
 */
@Entity
@Table(name = "bookings", schema = "public")
@Getter @Setter @ToString
public class Booking {
    /** уникальный идентификатор бронирования */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** дата начала бронирования */
    private LocalDate start;
    /** дата конца бронирования */
    private LocalDate end;
    /** вещь, которую пользователь бронирует */
    @Column(name = "item_id")
    private Long itemId;
    /** пользователь, который осуществляет бронирование */
    @Column(name = "booker_id")
    private Long bookerId;
    /** статус бронирования */
    @Enumerated(EnumType.STRING)
    private Status status;
}
