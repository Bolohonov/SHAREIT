package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Optional;

public interface BookingService {
    BookingDto addNew(Long userId, Booking booking);

    Optional<BookingDto> update(Long userId, Booking booking);

    Optional<BookingDto> patched(Long userId, Long bookingId, String booking);

    Optional<BookingDto> findById(Long bookingId);

    Collection<BookingDto> getUserBookings(Long userId);

    boolean delete(Long userId, Long bookingId);
}
