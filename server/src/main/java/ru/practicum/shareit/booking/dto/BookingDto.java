package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@ToString
public class BookingDto implements Serializable {
    /**
     * уникальный идентификатор вещи
     */
    private Long id;
    /**
     * дата начала бронирования
     */
    private LocalDateTime start;
    /**
     * дата конца бронирования
     */
    private LocalDateTime end;
    /**
     * вещь, которую пользователь бронирует
     */
    private Item item;
    /**
     * пользователь, который осуществляет бронирование
     */
    private Booker booker;
    /**
     * статус бронирования
     */
    private Status status;

    @AllArgsConstructor
    @ToString
    @Getter
    @Setter
    public static class Booker implements Serializable {
        private Long id;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    @Setter
    public static class Item implements Serializable {
        private Long id;
        private String name;
    }
}
