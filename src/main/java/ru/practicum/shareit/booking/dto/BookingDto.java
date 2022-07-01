package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDate;

/**
 BookingDto //
 */
@Data
public class BookingDto {
    /** уникальный идентификатор бронирования */
    private Long id;
    /** дата начала бронирования */
    private LocalDate start;
    /** дата конца бронирования */
    private LocalDate end;
    /** вещь, которую пользователь бронирует */
    private Item item;
    /** статус бронирования */
    private Status status;

    public BookingDto(LocalDate start, LocalDate end,
                      Long itemId, Status status) {
        this.start = start;
        this.end = end;
        this.item = new Item (itemId);

    }

    static class Item {
        /** уникальный идентификатор вещи */
        private Long id;
        /** краткое название */
        private String name;

        public Item(Long itemId) {
            this.id = itemId;
        }
    }
}
