package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * класс с описанием бронирования вещи - Booking //
 */
@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@ToString
public class Booking {
    /**
     * уникальный идентификатор бронирования
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * дата начала бронирования
     */
    @Column(name = "start_date_time")
    private LocalDateTime start;
    /**
     * дата конца бронирования
     */

    private LocalDateTime end;
    /**
     * вещь, которую пользователь бронирует
     */
    @Column(name = "item_id")
    private Long itemId;
    /**
     * пользователь, который осуществляет бронирование
     */
    @Column(name = "booker_id")
    private Long bookerId;
    /**
     * статус бронирования
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private Status status;
}
