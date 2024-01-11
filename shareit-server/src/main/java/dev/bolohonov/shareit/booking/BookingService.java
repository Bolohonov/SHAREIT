package dev.bolohonov.shareit.booking;

import dev.bolohonov.shareit.booking.dto.BookingDto;

import java.util.Collection;
import java.util.Optional;

public interface BookingService {
    BookingDto addNew(Long userId, Booking booking);

    Optional<BookingDto> approveOrRejectBooking(Long userId, Long bookingId, Boolean approved);

    Optional<BookingDto> findBookingById(Long userId, Long bookingId);

    Collection<BookingDto> getUserBookings(Long userId, State state, Integer from, Integer size);

    Collection<BookingDto> getBookingsByOwner(Long userId, State state, Integer from, Integer size);
}
