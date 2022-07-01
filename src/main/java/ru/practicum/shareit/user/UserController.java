package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collection;

import static org.springframework.http.HttpStatus.*;

/**
 * // TODO .
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(OK)
    public Collection<User> getAllUsers() {
        return userService.getUsers();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public User saveNewUser(@Valid @RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping
    @ResponseStatus(OK)
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user.getId(), user).orElseThrow(() -> {
            log.warn("пользователь с id {} не найден для обновления", user.getId());
            throw new ResponseStatusException(BAD_REQUEST);
        });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        if (!userService.deleteUser(id)) {
            log.warn("режиссер с id {} не найден для удаления", id);
            throw new ResponseStatusException(BAD_REQUEST);
        }
    }
}
