package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getUsers();

    User addUser(User user);

    boolean deleteUser(Long id);

    User updateUser(Long id, User user);

    Optional<User> findUserById(Long id);
}
