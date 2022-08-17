package ru.practicum.shareit.item;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemDtoWithoutComments;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.AccessToItemException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    @Override
    public ItemDto addNewItem(Long userId, Item item) {
        if (!userService.getUserById(userId).isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        item.setOwnerId(userId);
        return itemMapper.toItemDto(itemRepository.save(item),
                commentRepository.findCommentsByItemIdOrderByCreatedDesc(item.getId()));
    }

    @Override
    public Optional<ItemDto> updateItem(Long userId, Item item) {
        Item oldItem = itemRepository.findById(item.getId()).orElseThrow(() -> {
                    throw new UserNotFoundException("Пользователь не найден");
                }
        );
        if (!oldItem.getOwnerId().equals(userId)) {
            throw new AccessToItemException("Доступ запрещен!");
        }
        item.setOwnerId(userId);
        return ofNullable(itemMapper.toItemDto(itemRepository.save(item),
                commentRepository.findCommentsByItemIdOrderByCreatedDesc(item.getId())));
    }

    @Override
    public Optional<ItemDto> patchedItem(Long userId, Long itemId, String json) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
                    throw new UserNotFoundException("Пользователь не найден");
                }
        );
        JsonObject obj = new Gson().fromJson(json, JsonObject.class);
        Optional<String> name;
        Optional<String> description;
        Optional<Boolean> available;
        if (!item.getOwnerId().equals(userId)) {
            throw new AccessToItemException("Доступ запрещен!");
        }
        try {
            name = ofNullable(obj.get("name").getAsString());
            item.setName(name.get());
        } catch (NullPointerException e) {
            log.info("Часть полей полученного объекта пустые");
        }
        try {
            description = ofNullable(obj.get("description").getAsString());
            item.setDescription(description.get());
        } catch (NullPointerException e) {
            log.info("Часть полей полученного объекта пустые");
        }
        try {
            available = ofNullable(obj.get("available").getAsBoolean());
            item.setAvailable(available.get());
        } catch (NullPointerException e) {
            log.info("Часть полей полученного объекта пустые");
        }
        return ofNullable(itemMapper.toItemDto(itemRepository.save(item),
                commentRepository.findCommentsByItemIdOrderByCreatedDesc(itemId)));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ItemDtoWithBooking> findItemById(Long itemId, Long userId) {
        checkUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new ItemNotFoundException("Вещь не найдена");
        });

        return ofNullable(itemMapper.toItemDtoWithBooking(item,
                this.getLastAndNextBookingByItemIdAndUserId(itemId, userId)[0],
                this.getLastAndNextBookingByItemIdAndUserId(itemId, userId)[1],
                commentRepository.findCommentsByItemIdOrderByCreatedDesc(itemId)));
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDtoWithBooking> getUserItems(Long userId, Integer from, Integer size) {
        checkUser(userId);
        checkParams(from, size);
        Collection<ItemDtoWithBooking> itemsDto = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(this.getPageNumber(from, size), size,
                Sort.by("id").ascending());
        Iterable<Item> items = itemRepository.findByOwnerId(userId, pageRequest);
        for (Item i : items) {
            itemsDto.add(itemMapper.toItemDtoWithBooking(i,
                    this.getLastAndNextBookingByItemIdAndUserId(i.getId(), userId)[0],
                    this.getLastAndNextBookingByItemIdAndUserId(i.getId(), userId)[1],
                    commentRepository.findCommentsByItemIdOrderByCreatedDesc(i.getId())));
        }
        return itemsDto;
    }

    @Override
    public Collection<ItemDtoWithBooking> getAllUserItems(Long userId) {
        Collection<ItemDtoWithBooking> itemsDto = new ArrayList<>();
        for (Item i : itemRepository.findByOwnerId(userId)) {
            itemsDto.add(itemMapper.toItemDtoWithBooking(i,
                    this.getLastAndNextBookingByItemIdAndUserId(i.getId(), userId)[0],
                    this.getLastAndNextBookingByItemIdAndUserId(i.getId(), userId)[1],
                    commentRepository.findCommentsByItemIdOrderByCreatedDesc(i.getId())));
        }
        return itemsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> search(Long userId, String text, Integer from, Integer size) {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(this.getPageNumber(from, size), size,
                Sort.by("id").ascending());
        if (!text.isEmpty()) {
            for (Item i : itemRepository
                    .search(userId, text, pageRequest)) {
                itemsDto.add(itemMapper.toItemDto(i,
                        commentRepository.findCommentsByItemIdOrderByCreatedDesc(i.getId())));
            }
        } else {
            return Collections.emptyList();
        }
        return itemsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDto addComment(Long userId, Long itemId, Comment comment) {
        comment.setCreated(LocalDateTime.now());
        Optional<LocalDateTime> firstStartOfBookingDate = ofNullable(bookingRepository
                .findBookingByItemIdAndAndBookerId(itemId, userId,
                        Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST)).getStart());
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        if (firstStartOfBookingDate.isPresent() && firstStartOfBookingDate.get().isBefore(LocalDateTime.now())) {
            commentRepository.save(comment);
        } else {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        return commentMapper.toCommentDto(comment, userService.getUserById(userId).get().getName());
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()
                || itemRepository.findById(itemId).get().getOwnerId().equals(userId)) {
            throw new ResponseStatusException(BAD_REQUEST);
        } else {
            itemRepository.deleteById(itemId);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkOwner(Long userId, Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
                    throw new ItemNotFoundException("Вещь не найдена");
                }
        ).getOwnerId().equals(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDtoWithoutComments> findItemsByRequest(Long requestId) {
        Collection<Item> items = itemRepository.findByRequestId(requestId);
        return itemMapper.toItemDtoWithoutComments(items);
    }

    private Optional<Booking>[] getLastAndNextBookingByItemIdAndUserId(Long itemId, Long userId) {
        Collection<Booking> bookings = bookingRepository.findBookingByItemId(itemId);
        Optional<Booking> last = Optional.empty();
        Optional<Booking> next = Optional.empty();
        if (!bookings.isEmpty()) {
            if (this.checkOwner(userId, itemId) || bookingRepository.findBookingByItemIdAndAndBookerId(itemId, userId,
                    Sort.by(Sort.Direction.DESC, "bookerId")).isEmpty()) {
                last = bookings
                        .stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList())
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .findFirst();
                next = bookings
                        .stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList())
                        .stream()
                        .sorted(Comparator.comparing(Booking::getStart))
                        .findFirst();
            }
        }
        return new Optional[]{last, next};
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
