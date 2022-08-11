package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.ItemService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemService itemService;

    public static BookingDto toBookingDto(Booking booking, String itemName) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new BookingDto.Item(booking.getItemId(), itemName),
                new BookingDto.Booker(booking.getBookerId()),
                booking.getStatus()
        );
    }

    public Collection<BookingDto> toBookingDto(Iterable<Booking> bookings, Long userId) {
        Collection<BookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(this.toBookingDto(booking, itemService.findItemById(booking.getItemId(), userId).get().getName()));
        }

        return dtos;
    }
}
