package ru.practicum.shareit.booking;

public enum State {
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
