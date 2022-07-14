package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;
import java.util.Optional;

public class BookingServiceImpl implements BookingService {
    @Override
    public BookingDto addNew(Long userId, Booking booking) {
        return null;
    }

    @Override
    public Optional<BookingDto> update(Long userId, Booking booking) {
        return Optional.empty();
    }

    @Override
    public Optional<BookingDto> patched(Long userId, Long bookingId, String booking) {
        return Optional.empty();
    }

    @Override
    public Optional<BookingDto> findById(Long bookingId) {
        return Optional.empty();
    }

    @Override
    public Collection<BookingDto> getUserBookings(Long userId) {
        return null;
    }

    @Override
    public boolean delete(Long userId, Long bookingId) {
        return false;
    }
}
