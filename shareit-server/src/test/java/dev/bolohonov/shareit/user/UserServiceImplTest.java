package dev.bolohonov.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import dev.bolohonov.shareit.user.exceptions.UserNotFoundException;
import dev.bolohonov.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Test
    @SneakyThrows
    void testSaveUserSuccess() {
        User user = makeUser(1L, "Ivan", "ivan@yandex.ru");
        UserServiceImpl userService = new UserServiceImpl(userRepository);
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenReturn(user);
        User userToCompare = userService.saveUser(user);
        assertEquals(userToCompare.getId(), user.getId());
        assertEquals(userToCompare.getName(), user.getName());
        assertEquals(userToCompare.getEmail(), user.getEmail());
    }

    @Test
    @SneakyThrows
    void testGetUserByIdUserNotFoundException() {
        UserServiceImpl userService = new UserServiceImpl(userRepository);
        assertThrows(UserNotFoundException.class, () ->
                userService.getUserById(1L));
    }

    @Test
    @SneakyThrows
    void testGetUserByIdSuccess() {
        User user = makeUser(1L, "Ivan", "ivan@yandex.ru");
        UserServiceImpl userService = new UserServiceImpl(userRepository);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        User userToCompare = userService.getUserById(1L).get();
        assertEquals(userToCompare.getId(), user.getId());
        assertEquals(userToCompare.getName(), user.getName());
        assertEquals(userToCompare.getEmail(), user.getEmail());
    }

    @Test
    @SneakyThrows
    void testUpdateUserSuccess() {
        UserServiceImpl userService = new UserServiceImpl(userRepository);
        User userUpdated = makeUser(1L, "IvanUpdated", "ivan@yandex.ru");
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenReturn(userUpdated);
        User userToCompare = userService.updateUser(1L, userUpdated).get();
        assertEquals(userToCompare.getId(), userUpdated.getId());
        assertEquals(userToCompare.getName(), userUpdated.getName());
        assertEquals(userToCompare.getEmail(), userUpdated.getEmail());
    }

    @Test
    void testGetAllUsersSuccess() {
        User userFirst = makeUser(1L, "Ivan", "ivan@yandex.ru");
        User userSecond = makeUser(2L, "Ivan2", "ivan2@yandex.ru");
        UserServiceImpl userService = new UserServiceImpl(userRepository);
        List<User> listToCompare = new ArrayList<>();
        listToCompare.add(userFirst);
        listToCompare.add(userSecond);
        Mockito
                .when(userRepository.findAll())
                .thenReturn(listToCompare);
        List<User> listUsers = userService.getUsers()
                .stream()
                .sorted(Comparator.comparingLong(u -> u.getId()))
                .collect(Collectors.toList());
        assertEquals(listUsers.get(0).getId(), listToCompare.get(0).getId());
        assertEquals(listUsers.get(0).getName(), listToCompare.get(0).getName());
        assertEquals(listUsers.get(0).getEmail(), listToCompare.get(0).getEmail());
        assertEquals(listUsers.get(1).getId(), listToCompare.get(1).getId());
        assertEquals(listUsers.get(1).getName(), listToCompare.get(1).getName());
        assertEquals(listUsers.get(1).getEmail(), listToCompare.get(1).getEmail());
    }

    private User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}