package dev.bolohonov.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import dev.bolohonov.shareit.booking.Booking;
import dev.bolohonov.shareit.booking.repository.BookingRepository;
import dev.bolohonov.shareit.comment.Comment;
import dev.bolohonov.shareit.comment.dto.CommentDto;
import dev.bolohonov.shareit.comment.dto.CommentMapper;
import dev.bolohonov.shareit.comment.repository.CommentRepository;
import dev.bolohonov.shareit.item.dto.ItemDto;
import dev.bolohonov.shareit.item.dto.ItemDtoWithBooking;
import dev.bolohonov.shareit.item.dto.ItemDtoWithoutComments;
import dev.bolohonov.shareit.item.dto.ItemMapper;
import dev.bolohonov.shareit.item.exceptions.AccessToItemException;
import dev.bolohonov.shareit.item.exceptions.ItemNotFoundException;
import dev.bolohonov.shareit.item.repository.ItemRepository;
import dev.bolohonov.shareit.user.UserService;
import dev.bolohonov.shareit.user.exceptions.UserNotFoundException;

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
    public Optional<ItemDto> patchedItem(Long userId, Long itemId, Item newItem) {
        checkUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
                    throw new ItemNotFoundException("Вещь не найдена");
                }
        );
        Optional<String> name;
        Optional<String> description;
        Optional<Boolean> available;
        if (!item.getOwnerId().equals(userId)) {
            throw new AccessToItemException("Доступ запрещен!");
        }
        try {
            name = ofNullable(newItem.getName());
            if (name.isPresent()) {
                item.setName(name.get());
            }
        } catch (NullPointerException e) {
            log.info("Часть полей полученного объекта пустые");
        }
        try {
            description = ofNullable(newItem.getDescription());
            if (description.isPresent()) {
                item.setDescription(description.get());
            }
        } catch (NullPointerException e) {
            log.info("Часть полей полученного объекта пустые");
        }
        try {
            available = ofNullable(newItem.getAvailable());
            if (available.isPresent()) {
                item.setAvailable(available.get());
            }
        } catch (NullPointerException e) {
            log.info("Часть полей полученного объекта пустые");
        }
        return ofNullable(itemMapper.toItemDto(itemRepository.save(item),
                commentRepository.findCommentsByItemIdOrderByCreatedDesc(itemId)));
    }

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

    @Override
    public boolean checkOwner(Long userId, Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
                    throw new ItemNotFoundException("Вещь не найдена");
                }
        ).getOwnerId().equals(userId);
    }

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
