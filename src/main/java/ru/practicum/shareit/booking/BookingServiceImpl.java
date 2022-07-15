package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exceptions.AccessToBookingException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.AccessToItemException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.util.Optional.of;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto addNew(Long userId, Booking booking) {
        if (!userService.getUserById(userId).isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        booking.setBookerId(userId);
        booking.setStatus(Status.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public Optional<BookingDto> approveOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
        if (approved == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!userService.getUserById(userId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        });
        if (!itemService.checkOwner(userId, booking.getItemId())) {
            throw new AccessToItemException("У пользователя нет доступа к данной функции!");
        }
        if (approved.equals(Boolean.TRUE)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return of(bookingMapper.toBookingDto(bookingRepository.save(booking)));
    }

    @Override
    public Optional<BookingDto> findBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                });
        if (!booking.getBookerId().equals(userId)) {
            if (!itemService.checkOwner(userId, booking.getItemId())) {
                throw new AccessToBookingException("Доступ к бронированию для данного пользователя закрыт!");
            }
        }
        return of(bookingMapper.toBookingDto(booking));
    }

    @Override
    public Collection<BookingDto> getUserBookings(Long userId, String state) {
        Collection<BookingDto> bookingsDto = new ArrayList<>();


        if (!text.isEmpty()) {
            for (Item i : itemRepository.searchItems(userId, text)) {
                itemsDto.add(itemMapper.toItemDto(i));
            }
        } else {
            return Collections.emptyList();
        }
        return itemsDto;
    }

    @Override
    public Collection<BookingDto> getBookingsByOwner(Long userId, String state) {
        return null;
    }

    @Override
    public boolean delete(Long userId, Long bookingId) {
        return false;
    }
}
