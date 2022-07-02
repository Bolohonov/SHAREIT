package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collection;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
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
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user.getId(), user).orElseThrow(() -> {
            log.warn("пользователь с id {} не найден", user.getId());
            throw new ResponseStatusException(NOT_FOUND);
        });
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public User findUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .orElseThrow(() -> {
                    log.warn("пользователь с id {} не найден", id);
                    throw new ResponseStatusException(NOT_FOUND);
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(OK)
    public void deleteUser(@PathVariable Long id) {
        if (!userService.deleteUser(id)) {
            log.warn("режиссер с id {} не найден для удаления", id);
            throw new ResponseStatusException(BAD_REQUEST);
        }
    }
}
