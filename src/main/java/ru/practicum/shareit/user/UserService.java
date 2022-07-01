package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.of;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;
    public Collection<User> getUsers() {
        return userRepository.getUsers();
    }

    public User saveUser(User user) {
        if (validateEmailNotDuplicated(user)) {
            userRepository.addUser(user);
            log.info("User has been saved");
        }
        return user;
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findUserById(userId);
    }

    public Optional<User> updateUser(Long id, User user) {
        return of(userRepository.updateUser(id, userRepository.findUserById(id).get()));
    }

    public boolean deleteUser(Long userId) {
        return userRepository.deleteUser(userId);
    }

    private boolean validateEmailNotDuplicated(User user) {
        for (User u : userRepository.getUsers()) {
            if (u.getEmail().equals(user.getEmail())) {
                log.warn("Duplicated email");
                throw new ValidationException(String.format("Пользователь с электронной почтой %s" +
                        " уже зарегистрирован.", user.getEmail()));
            }
        }
        return true;
    }
}
