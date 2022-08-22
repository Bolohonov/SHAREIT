package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
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

    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

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
        checkUser(userId);
        if (approved == null) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        });
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        if (!itemService.checkOwner(userId, booking.getItemId())) {
            throw new AccessToItemException("У пользователя нет доступа к данной функции!");
        }
        if (approved.equals(Boolean.TRUE)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return ofNullable(bookingMapper.toBookingDto(bookingRepository.save(booking),
                itemService.findItemById(booking.getItemId(), userId).get().getName()));
    }

    @Override
    public Optional<BookingDto> findBookingById(Long userId, Long bookingId) {
        checkUser(userId);
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
        return ofNullable(bookingMapper.toBookingDto(booking,
                itemService.findItemById(booking.getItemId(), userId).get().getName()));
    }

    @Override
    public Collection<BookingDto> getUserBookings(Long userId, State state, Integer from, Integer size) {
        checkUser(userId);
        checkParams(from, size);
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        Collection<Booking> bookingsPage = new ArrayList<>();
        Iterable<Booking> bookings;
        PageRequest pageRequest = PageRequest.of(this.getPageNumber(from, size), size,
                SORT_BY_START_DESC);
        Clock secondTickingClock = Clock.tickSeconds(ZoneId.systemDefault());
        switch (state) {
            case ALL:
                bookings = bookingRepository.findBookingByBookerId(userId, pageRequest);
                bookingsDto = bookingMapper.toBookingDto(bookings, userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingByBookerIdAndCurrent(userId,
                        pageRequest);
                bookingsDto = bookingMapper.toBookingDto(bookings, userId);
                break;
            case PAST:
                bookings = bookingRepository.findBookingByBookerIdAndEndIsBefore(userId,
                        LocalDateTime.now(secondTickingClock), pageRequest);
                bookingsDto = bookingMapper.toBookingDto(bookings, userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingByBookerIdAndStartIsAfter(userId,
                        LocalDateTime.now(secondTickingClock), pageRequest);
                bookingsDto = bookingMapper.toBookingDto(bookings, userId);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingByBookerId(userId, pageRequest);
                bookings.forEach(bookingsPage::add);
                bookingsDto = this.filterBookingsByStatusSortByStart(bookingsPage, Status.WAITING, userId);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingByBookerId(userId, pageRequest);
                bookings.forEach(bookingsPage::add);
                bookingsDto = this.filterBookingsByStatusSortByStart(bookingsPage, Status.REJECTED, userId);
                break;
        }
        return bookingsDto;
    }

    @Override
    public Collection<BookingDto> getBookingsByOwner(Long userId, State state, Integer from, Integer size) {
        checkUser(userId);
        checkParams(from, size);
        Collection<ItemDtoWithBooking> itemsOfUser = itemService.getAllUserItems(userId);
        Iterable<Booking> bookingPage;
        Collection<Booking> bookings = new ArrayList<>();
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(this.getPageNumber(from, size), size,
                Sort.by("id").ascending());
        Predicate<Booking> function;
        if (itemsOfUser.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        for (ItemDtoWithBooking itemDto : itemsOfUser) {
            bookingPage = bookingRepository.findBookingByItemId(itemDto.getId(), pageRequest);
            bookingPage.forEach(bookings::add);
        }
        switch (state) {
            case ALL:
                bookingsDto = bookings.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(b -> bookingMapper.toBookingDto(b,
                                itemService.findItemById(b.getItemId(), userId).get().getName()))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                function = (b -> ((b.getStart().isBefore(LocalDateTime.now())
                        || b.getStart() == LocalDateTime.now()) &&
                        b.getEnd().isAfter(LocalDateTime.now())));
                bookingsDto = this.filterBookingsAndSortByTime(bookings, function, userId);
                break;
            case PAST:
                function = b -> b.getEnd().isBefore(LocalDateTime.now());
                bookingsDto = this.filterBookingsAndSortByTime(bookings, function, userId);
                break;
            case FUTURE:
                function = b -> b.getStart().isAfter(LocalDateTime.now());
                bookingsDto = this.filterBookingsAndSortByTime(bookings, function, userId);
                break;
            case WAITING:
                bookingsDto = this.filterBookingsByStatusSortByStart(bookings, Status.WAITING, userId);
                break;
            case REJECTED:
                bookingsDto = this.filterBookingsByStatusSortByStart(bookings, Status.REJECTED, userId);
                break;
        }
        return bookingsDto;
    }

    private void validateBooking(Long userId, Booking booking) {
        if (!userService.getUserById(userId).isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (!itemService.findItemById(booking.getItemId(), userId).isPresent()) {
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (itemService.checkOwner(userId, booking.getItemId())) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        if (itemService.findItemById(booking.getItemId(), userId).get().getAvailable().equals(Boolean.FALSE)) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        if (booking.getStart().isBefore(LocalDateTime.now())
                || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
    }

    private Collection<BookingDto> filterBookingsByStatusSortByStart(Collection<Booking> bookings,
                                                                     Status status, Long userId) {
        return bookings
                .stream()
                .filter((b) -> b.getStatus().equals(status))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(b -> bookingMapper.toBookingDto(b,
                        itemService.findItemById(b.getItemId(), userId).get().getName()))
                .collect(Collectors.toList());
    }

    private Collection<BookingDto> filterBookingsAndSortByTime(Collection<Booking> bookings,
                                                               Predicate<Booking> func, Long userId) {
        List<Booking> bookingList = bookings
                .stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .filter(func)
                .collect(Collectors.toList());
        ;
        return bookingMapper.toBookingDto(bookingList, userId);
    }

    private void checkUser(Long userId) {
        if (!userService.getUserById(userId).isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    private void checkParams(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
    }

    private Integer getPageNumber(Integer from, Integer size) {
        return from % size;
    }
}
