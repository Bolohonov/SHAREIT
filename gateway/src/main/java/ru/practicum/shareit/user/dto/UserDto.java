package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserDto {
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
