package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.exceptions.ValidationEmailDuplicated;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.of;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User saveUser(User user) {
        validateEmailNotDuplicated(user);
        return userRepository.addUser(user);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        if (userRepository.findUserById(userId).isPresent()) {
            return userRepository.findUserById(userId);
        } else {
            log.warn("пользователь с id {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public Optional<User> updateUser(Long id, User user) {
        validateEmailNotDuplicated(user);
        if (userRepository.findUserById(id).isPresent()) {
            return of(userRepository.updateUser(id, this.compareToUpdate(id, user)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteUser(Long userId) {
        return userRepository.deleteUser(userId);
    }

    private void validateEmailNotDuplicated(User user) {
        for (User u : userRepository.getUsers()) {
            if (u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId())) {
                log.warn("Duplicated email");
                throw new ValidationEmailDuplicated(String.format("Пользователь с электронной почтой %s" +
                        " уже зарегистрирован.", user.getEmail()));
            }
        }
    }

    private User compareToUpdate(Long id, User user) {
        if (userRepository.findUserById(id).isPresent()) {
            User oldUser = userRepository.findUserById(id).get();
            if (user.getName() == null) {
                user.setName(oldUser.getName());
            }
            if (user.getEmail() == null) {
                user.setEmail(oldUser.getEmail());
            }
            user.setId(id);
        }
        return user;
    }
}
