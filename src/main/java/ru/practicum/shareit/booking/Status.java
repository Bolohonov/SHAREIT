package ru.practicum.shareit.booking;

import javax.persistence.Enumerated;

public enum Status {
    /** новое бронирование, ожидает одобрения */
    WAITING,
    /** бронирование подтверждено владельцем */
    APPROVED,
    /** бронирование отклонено владельцем */
    REJECTED,
    /** бронирование отменено создателем */
    CANCELED
}
