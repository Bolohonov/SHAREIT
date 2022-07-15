package ru.practicum.shareit.booking.dto;

import lombok.Data;

@Data
public enum StateDto {
    /** все бронирования */
    ALL,
    /** текущие бронирования */
    CURRENT,
    /** завершенные бронирования */
    PAST,
    /** будущие бронирования */
    FUTURE,
    /** ожидающие подтверждения бронирования */
    WAITING,
    /** отклоненные бронирования */
    REJECTED
}
