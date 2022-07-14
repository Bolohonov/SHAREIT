package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

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
    public BookingDto patchedBooking(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestBody String json) {
        return bookingService.patched(userId, bookingId, json).orElseThrow(() -> {
            log.warn("запрос с id {} не найден для обновления", bookingId);
            throw new ResponseStatusException(BAD_REQUEST);
        });
    }
}
