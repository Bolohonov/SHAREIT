package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exceptions.AccessToBookingException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.exceptions.AccessToItemException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Collection<BookingDto> getUserBookings(Long userId, State state) {
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        switch(state) {
            case ALL:
                bookingsDto = bookingRepository.findBookingByBookerId(userId,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(b -> bookingMapper.toBookingDto(b))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookingsDto = bookingRepository.findBookingByBookerIdAndEndIsAfter(userId,
                                LocalDate.now(ZoneId.of("GMT+03:00")),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(b -> bookingMapper.toBookingDto(b))
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookingsDto = bookingRepository.findBookingByBookerIdAndEndIsBefore(userId,
                                LocalDate.now(ZoneId.of("GMT+03:00")),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(b -> bookingMapper.toBookingDto(b))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookingsDto = bookingRepository.findBookingByBookerIdAndStartIsAfter(userId,
                                LocalDate.now(ZoneId.of("GMT+03:00")),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(b -> bookingMapper.toBookingDto(b))
                        .collect(Collectors.toList());
                break;
//            case WAITING:
//                bookingsDto = bookingRepository.findBookingByBookerIdAndStatusWaiting(userId,
//                                Sort.by(Sort.Direction.DESC, "start"))
//                        .stream()
//                        .map(b -> bookingMapper.toBookingDto(b))
//                        .collect(Collectors.toList());
//                break;
//            case REJECTED:
//                bookingsDto = bookingRepository.findBookingByBookerIdAndStatusRejected(userId,
//                                Sort.by(Sort.Direction.DESC, "start"))
//                        .stream()
//                        .map(b -> bookingMapper.toBookingDto(b))
//                        .collect(Collectors.toList());
//                break;
        }
        return bookingsDto;
    }

    @Override
    public Collection<BookingDto> getBookingsByOwner(Long userId, State state) {
        Collection<ItemDtoWithBooking> itemsOfUser = itemService.getUserItems(userId);
        Collection<Booking> bookings = Collections.emptyList();
        Collection<BookingDto> bookingsDto = Collections.emptyList();
        if (itemsOfUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        for (ItemDtoWithBooking itemDto : itemsOfUser) {
            bookings.addAll(bookingRepository.findBookingByItemId(itemDto.getId()));
        }
        switch(state) {
            case ALL:
                bookingsDto = bookings.stream().map(b -> bookingMapper.toBookingDto(b))
                    .collect(Collectors.toList());
                break;
            case CURRENT:
                bookingsDto = bookings.stream().filter((b) -> b.getEnd().isAfter(LocalDate.now()))
                        .map(b -> bookingMapper.toBookingDto(b))
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookingsDto = bookings.stream().filter((b) -> b.getEnd().isBefore(LocalDate.now()))
                        .map(b -> bookingMapper.toBookingDto(b))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookingsDto = bookings.stream().filter((b) -> b.getStart().isAfter(LocalDate.now()))
                        .map(b -> bookingMapper.toBookingDto(b))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookingsDto = bookings.stream().filter((b) -> b.getStatus().equals(Status.WAITING))
                        .map(b -> bookingMapper.toBookingDto(b))
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookingsDto = bookings.stream().filter((b) -> b.getStatus().equals(Status.REJECTED))
                        .map(b -> bookingMapper.toBookingDto(b))
                        .collect(Collectors.toList());
                break;
        }
        return bookingsDto;
    }
}
