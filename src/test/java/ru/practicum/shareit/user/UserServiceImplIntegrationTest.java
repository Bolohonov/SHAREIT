package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    private final EntityManager em;
    private final UserService userservice;

    @Test
    void saveUser() {
        User user = makeUser("Ivan", "ivan@yandex.ru");
        User userSecond = makeUser("Ivan2", "ivan2@yandex.ru");
        userservice.saveUser(user);
        userservice.saveUser(userSecond);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userToCompare = query.setParameter("id", 1L).getSingleResult();
        User userToCompareSecond = query.setParameter("id", 2L).getSingleResult();
        assertEquals(user, userToCompare);
        assertEquals(userSecond, userToCompareSecond);
        assertEquals(user.getName(), "Ivan");
        assertEquals(userSecond.getName(), "Ivan2");
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}