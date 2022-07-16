package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findBookingByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);
    Collection<Booking> findBookingByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);
    Collection<Booking> findBookingByBookerIdAndEndIsAfter(Long bookerId, LocalDateTime end, Sort sort);
    Collection<Booking> findBookingByBookerId(Long bookerId, Sort sort);
    Collection<Booking> findBookingByBookerIdAndStatusIsWaiting(Long bookerId, Sort sort);
    Collection<Booking> findBookingByBookerIdAndStatusIsRejected(Long bookerId, Sort sort);
    Collection<Booking> findBookingByItemId(Long itemId);
}
