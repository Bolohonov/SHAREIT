package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.*;

import static java.util.Optional.of;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(appointId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        if (!users.containsKey(id)) {
            return false;
        }
        users.remove(id);
        return true;
    }

    @Override
    public User updateUser(Long id, User user) {
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        Optional<User> user;
        try {
            user = Optional.ofNullable(users.get(id));
        } catch (Exception exp) {
            log.warn("Пользователь с id {} не найден", id);
            throw new UserNotFoundException(exp.getMessage());
        }
        return user;
    }

    private Long appointId() {
        ++id;
        return id;
    }
}
