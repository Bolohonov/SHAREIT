package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * класс с описанием пользователя - User //
 */
@Entity
@Table(name = "users", schema = "public")
@Getter @Setter @ToString
public class User {
    /**
     * уникальный идентификатор пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
