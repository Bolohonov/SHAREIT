package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    @ResponseStatus(OK)
    public ResponseEntity<Object> createNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestBody @Validated ItemRequestDto request) {
        return requestClient.addNewRequest(userId, request);
    }

    @GetMapping("/all")
    @ResponseStatus(OK)
    public ResponseEntity<Object> findAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PositiveOrZero @RequestParam(name = "from",
                                                              defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size",
                                                              defaultValue = "10") Integer size) {
        return requestClient.getRequests(userId, from, size);
    }

    @GetMapping
    @ResponseStatus(OK)
    public ResponseEntity<Object> findAllRequestsOfUser(@RequestHeader("X-Sharer-User-Id")
                                                                         Long userId) {
        return requestClient.getRequestsByUser(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(OK)
    public ResponseEntity<Object> findRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PathVariable Long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }
}
