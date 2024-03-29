package dev.bolohonov.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import dev.bolohonov.shareit.requests.dto.ItemRequestDto;
import dev.bolohonov.shareit.requests.dto.ItemRequestDtoWithResponses;

import java.util.Collection;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/requests")
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(OK)
    public ItemRequestDto createNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody ItemRequest request) {
        return requestService.addNewRequest(userId, request);
    }

    @GetMapping("/all")
    @ResponseStatus(OK)
    public Collection<ItemRequestDto> findAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(value = "from", defaultValue =
                                                                  "0") Integer from,
                                                          @RequestParam(value = "size", defaultValue =
                                                                  "50") Integer size) {
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping
    @ResponseStatus(OK)
    public Collection<ItemRequestDtoWithResponses> findAllRequestsOfUser(@RequestHeader("X-Sharer-User-Id")
                                                                         Long userId) {
        return requestService.findRequestsByUser(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(OK)
    public ItemRequestDtoWithResponses findRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PathVariable Long requestId) {
        return requestService.findRequestById(requestId, userId).get();
    }
}
