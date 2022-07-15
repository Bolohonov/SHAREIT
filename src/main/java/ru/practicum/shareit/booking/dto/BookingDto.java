package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDate;

@Data
public class BookingDto {
    /**
     * дата начала бронирования
     */
    private LocalDate start;
    /**
     * дата конца бронирования
     */
    private LocalDate end;
    /**
     * вещь, которую пользователь бронирует
     */
    private Long itemId;
    /**
     * статус бронирования
     */
    private Status status;

    public BookingDto(LocalDate start, LocalDate end,
                      Long itemId, Status status) {
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.status = status;
    }
}
