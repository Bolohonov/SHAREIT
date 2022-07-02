package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * класс с описанием пользователя - User //
 */
@Data
@Builder
public class User {
    /**
     * уникальный идентификатор пользователя
     */
    private Long id;
    /**
     * имя или логин пользователя
     */
    @NotBlank
    private String name;
    /**
     * адрес электронной почты
     */
    @Email
    @NotBlank
    private String email;
}
