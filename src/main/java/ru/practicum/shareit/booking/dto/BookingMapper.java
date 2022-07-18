package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemService;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking, String itemName) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new BookingDto.Item(booking.getItemId(), itemName),
                new BookingDto.Booker(booking.getId()),
                booking.getStatus()
        );
    }
}
