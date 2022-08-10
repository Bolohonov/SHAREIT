package ru.practicum.shareit.requests.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoWithoutComments;
import ru.practicum.shareit.requests.ItemRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public Collection<ItemRequestDto> toItemRequestDto(Iterable<ItemRequest> requests) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            dtos.add(toItemRequestDto(request));
        }
        return dtos;
    }

    public static ItemRequestDtoWithResponses toItemRequestDtoWithResponses(ItemRequestDto request,
                Collection<ItemDtoWithoutComments> items) {
        Collection<ItemRequestDtoWithResponses.Response> responsesList = new ArrayList<>();
        for (ItemDtoWithoutComments item : items) {
            responsesList.add(new ItemRequestDtoWithResponses.Response(
                    item.getId(), item.getName(), item.getDescription()
            ));
        }
        return new ItemRequestDtoWithResponses(
                request.getDescription(),
                request.getCreated(),
                responsesList
        );
    }
}
