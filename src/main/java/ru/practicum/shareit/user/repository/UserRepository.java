package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getUsers();

    User addUser(User user);

    void deleteUser(Long id);

    User updateUser(Long id, User user);

    Optional<User> findUserById(Long id);
}
