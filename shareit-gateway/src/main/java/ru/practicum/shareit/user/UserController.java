package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    @ResponseStatus(OK)
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Object> saveNewUser(@RequestBody @Validated UserDto user) {
        log.info("Create new user");
        return userClient.saveUser(user);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(OK)
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody UserDto user) {
        log.info("Update user");
        return userClient.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(OK)
    public ResponseEntity<Object> findUserById(@PathVariable Long userId) {
        log.info("Get user with ID {}", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(OK)
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Delete user with ID {}", userId);
        return userClient.deleteUser(userId);
    }
}
