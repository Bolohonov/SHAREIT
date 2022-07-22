package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        return userService.updateUser(user.getId(), user).get();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(OK)
    public User patchedUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user).get();
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public User findUserById(@PathVariable Long id) {
        return userService.getUserById(id).get();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(OK)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
