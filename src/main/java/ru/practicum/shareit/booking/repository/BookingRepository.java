package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findBookingByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findBookingByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start,
                                                       Pageable pageable);

    Page<Booking> findBookingByBookerIdAndEndIsAfter(Long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findBookingByBookerId(Long bookerId, Pageable pageable);

    Collection<Booking> findBookingByItemId(Long itemId);

    Page<Booking> findBookingByItemId(Long itemId, Pageable pageable);

    Collection<Booking> findBookingByItemIdAndAndBookerId(Long itemId, Long bookerId, Sort sort);
}
