package ru.practicum.shareit.requests.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.requests.ItemRequest;

@Component
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }
}
