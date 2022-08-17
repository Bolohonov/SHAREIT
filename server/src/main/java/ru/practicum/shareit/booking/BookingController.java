package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(CREATED)
    public BookingDto saveNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody Booking booking) {
        return bookingService.addNew(userId, booking);
    }

    @PatchMapping("{bookingId}")
    @ResponseStatus(OK)
    public BookingDto approveOrRejectBooking(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam Boolean approved) {
        return bookingService.approveOrRejectBooking(userId, bookingId, approved).get();
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(OK)
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) {
        return bookingService.findBookingById(userId, bookingId).get();
    }

    @GetMapping
    @ResponseStatus(OK)
    public Collection<BookingDto> findBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") State state,
                                                    @RequestParam(value = "from", defaultValue =
                                                            "0") Integer from,
                                                    @RequestParam(value = "size", defaultValue =
                                                            "50") Integer size) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(OK)
    public Collection<BookingDto> findBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") State state,
                                                     @RequestParam(value = "from", defaultValue =
                                                             "0") Integer from,
                                                     @RequestParam(value = "size", defaultValue =
                                                             "50") Integer size) {
        return bookingService.getBookingsByOwner(userId, state, from, size);
    }
}
