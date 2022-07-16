package ru.practicum.shareit.item.dto;

import java.time.LocalDate;

public class ItemDtoWithBooking {
    /** уникальный идентификатор вещи */
    private Long id;
    /**
     * краткое название
     */
    private String name;
    /**
     * развёрнутое описание
     */
    private String description;
    /**
     * статус о том, доступна или нет вещь для аренды
     */
    private Boolean available;
    /**
     * если вещь была создана по запросу другого пользователя,
     * то в этом поле будет храниться ссылка на соответствующий запрос
     */
    private Long request;
    /**
     * дата окончания последнего бронирования
     */
    private LocalDate lastBookingDate;
    /**
     * дата начала следующего бронирования
     */
    private LocalDate nextBookingDate;


    public ItemDtoWithBooking(Long id, String name, String description, boolean available, Long requestId,
                              LocalDate lastBookingDate, LocalDate nextBookingDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = requestId;
        this.lastBookingDate = lastBookingDate;
        this.nextBookingDate = nextBookingDate;
    }
}
