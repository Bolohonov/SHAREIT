package ru.practicum.shareit.user;

import java.util.*;

import static java.util.Optional.of;

public class UserInMemoryRepository implements UserRepository{
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(getId());
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
        return users.put(id, user);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return of(users.get(id));
    }

    private long getId() {
        long lastId = users.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
