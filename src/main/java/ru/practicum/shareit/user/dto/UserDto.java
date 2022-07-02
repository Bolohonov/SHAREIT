package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 класс с описанием пользователя - User //
 */
@Data
public class UserDto {
    /** уникальный идентификатор пользователя */
    private Long id;
    /** имя или логин пользователя */
    private String name;
    /** адрес электронной почты */
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
