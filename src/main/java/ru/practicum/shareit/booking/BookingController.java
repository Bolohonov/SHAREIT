package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;

import javax.validation.Valid;

import java.util.Collection;

import static org.springframework.http.HttpStatus.*;

/**
 * // TODO .
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(CREATED)
    public BookingDto saveNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Valid @RequestBody Booking booking) {
        return bookingService.addNew(userId, booking);
    }

    @PatchMapping("{bookingId}")
    @ResponseStatus(OK)
    public BookingDto approveOrRejectBooking(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam Boolean approved) {
        return bookingService.approveOrRejectBooking(userId, bookingId, approved).orElseThrow(() -> {
            log.warn("запрос с id {} не найден для обновления", bookingId);
            throw new ResponseStatusException(BAD_REQUEST);
        });
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(OK)
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long id) {
        return bookingService.findBookingById(userId, id)
                .orElseThrow(() -> {
                    log.warn("предмет с id {} не найден", id);
                    throw new ResponseStatusException(NOT_FOUND);
                });
    }

    @GetMapping
    @ResponseStatus(OK)
    public Collection<BookingDto> findBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(OK)
    public Collection<BookingDto> findBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getBookingsByOwner(userId, state);
    }
}
