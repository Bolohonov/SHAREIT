package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.*;

import static java.util.Optional.of;

@Component
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository{
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
        return of(users.get(id));
    }

    private Long appointId() {
        ++id;
        return id;
    }
}
