package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Optional;

public interface BookingService {
    BookingDto addNew(Long userId, Booking booking);

    Optional<BookingDto> approveOrRejectBooking(Long userId, Long bookingId, Boolean approved);

    Optional<BookingDto> findBookingById(Long userId, Long bookingId);

    Collection<BookingDto> getUserBookings(Long userId, String state);

    Collection<BookingDto> getBookingsByOwner(Long userId, String state);

    boolean delete(Long userId, Long bookingId);
}
