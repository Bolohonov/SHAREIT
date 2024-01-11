package dev.bolohonov.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    Collection<User> getUsers();

    User saveUser(User user);

    Optional<User> getUserById(Long userId);

    Optional<User> updateUser(Long id, User user);

    void deleteUser(Long userId);
}
