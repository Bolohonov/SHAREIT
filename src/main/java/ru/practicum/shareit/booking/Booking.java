package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 класс с описанием бронирования вещи - Booking //
 */
@Data
public class Booking {
    /** уникальный идентификатор бронирования */
    private Long id;
    /** дата начала бронирования */
    private LocalDate start;
    /** дата конца бронирования */
    private LocalDate end;
    /** вещь, которую пользователь бронирует */
    private Item item;
    /** пользователь, который осуществляет бронирование */
    private User booker;
    /** статус бронирования */
    private Status status;
}
