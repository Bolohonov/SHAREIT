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
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.of;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
        validateBooking(userId, booking);
        booking.setBookerId(userId);
        booking.setStatus(Status.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(booking),
                itemService.findItemById(booking.getItemId(), userId).get().getName());
    }

    @Override
    public Optional<BookingDto> approveOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
        if (approved == null) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        });
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        if (!userService.getUserById(userId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (!itemService.checkOwner(userId, booking.getItemId())) {
            throw new AccessToItemException("У пользователя нет доступа к данной функции!");
        }
        if (approved.equals(Boolean.TRUE)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return of(bookingMapper.toBookingDto(bookingRepository.save(booking),
                itemService.findItemById(booking.getItemId(), userId).get().getName()));
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
        return of(bookingMapper.toBookingDto(booking,
                itemService.findItemById(booking.getItemId(), userId).get().getName()));
    }

    @Override
    public Collection<BookingDto> getUserBookings(Long userId, State state) {
        Collection<BookingDto> bookingsDto = Collections.emptyList();
        switch (state) {
            case ALL:
                bookingsDto = bookingRepository.findBookingByBookerId(userId,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookingsDto = bookingRepository.findBookingByBookerIdAndEndIsAfter(userId,
                                LocalDate.now(ZoneId.of("GMT+03:00")),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookingsDto = bookingRepository.findBookingByBookerIdAndEndIsBefore(userId,
                                LocalDate.now(ZoneId.of("GMT+03:00")),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookingsDto = bookingRepository.findBookingByBookerIdAndStartIsAfter(userId,
                                LocalDate.now(ZoneId.of("GMT+03:00")),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookingsDto = bookingRepository.findBookingByBookerId(userId,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .filter(b -> b.getStatus().equals(Status.WAITING))
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookingsDto = bookingRepository.findBookingByBookerId(userId,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .filter(b -> b.getStatus().equals(Status.REJECTED))
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
        }
        return bookingsDto;
    }

    @Override
    public Collection<BookingDto> getBookingsByOwner(Long userId, State state) {
        Collection<ItemDtoWithBooking> itemsOfUser = itemService.getUserItems(userId);
        Collection<Booking> bookings = new ArrayList<>();
        Collection<BookingDto> bookingsDto = Collections.emptyList();
        if (itemsOfUser.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        for (ItemDtoWithBooking itemDto : itemsOfUser) {
            bookings.addAll(bookingRepository.findBookingByItemId(itemDto.getId()));
        }
        switch (state) {
            case ALL:
                bookingsDto = bookings
                        .stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookingsDto = bookings
                        .stream()
                        .filter((b) -> b.getEnd().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookingsDto = bookings
                        .stream()
                        .filter((b) -> b.getEnd().isBefore(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookingsDto = bookings
                        .stream()
                        .filter((b) -> b.getStart().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookingsDto = bookings
                        .stream()
                        .filter((b) -> b.getStatus().equals(Status.WAITING))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookingsDto = bookings
                        .stream()
                        .filter((b) -> b.getStatus().equals(Status.REJECTED))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
        }
        return bookingsDto;
    }

    private void validateBooking(Long userId, Booking booking) {
        if (!itemService.findItemById(booking.getItemId(), userId).isPresent()) {
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (itemService.checkOwner(userId, booking.getItemId())) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        if (!userService.getUserById(userId).isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (itemService.findItemById(booking.getItemId(), userId).get().getAvailable().equals(Boolean.FALSE)) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        if (booking.getStart().isBefore(LocalDateTime.now())
                || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
    }
}
