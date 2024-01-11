package dev.bolohonov.shareit.requests.dto;

import org.springframework.stereotype.Component;
import dev.bolohonov.shareit.item.dto.ItemDtoWithoutComments;
import dev.bolohonov.shareit.requests.ItemRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public static ItemRequestDtoWithResponses toItemRequestDtoWithResponses(ItemRequest request,
                                                                            Collection<ItemDtoWithoutComments> items) {
        Collection<ItemRequestDtoWithResponses.Response> responsesList = new ArrayList<>();
        if (!items.equals(null)) {
            for (ItemDtoWithoutComments item : items) {
                responsesList.add(new ItemRequestDtoWithResponses.Response(
                        item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getRequestId()
                ));
            }
        }
        return new ItemRequestDtoWithResponses(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                responsesList
        );
    }
}
