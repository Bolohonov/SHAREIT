package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDate;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findBookingByBookerIdAndEndIsBefore(Long bookerId, LocalDate end, Sort sort);

    Collection<Booking> findBookingByBookerIdAndStartIsAfter(Long bookerId, LocalDate start, Sort sort);

    Collection<Booking> findBookingByBookerIdAndEndIsAfter(Long bookerId, LocalDate end, Sort sort);

    Collection<Booking> findBookingByBookerId(Long bookerId, Sort sort);

    Collection<Booking> findBookingByItemId(Long itemId);

    Collection<Booking> findBookingByItemIdAndAndBookerId(Long itemId, Long bookerId, Sort sort);
}
