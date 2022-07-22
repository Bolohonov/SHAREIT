package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findBookingByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    Collection<Booking> findBookingByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    Collection<Booking> findBookingByBookerIdAndEndIsAfter(Long bookerId, LocalDateTime end, Sort sort);

    Collection<Booking> findBookingByBookerId(Long bookerId, Sort sort);

    Collection<Booking> findBookingByItemId(Long itemId);

    Collection<Booking> findBookingByItemIdAndAndBookerId(Long itemId, Long bookerId, Sort sort);
}
