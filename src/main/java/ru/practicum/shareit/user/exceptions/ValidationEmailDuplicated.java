package ru.practicum.shareit.user.exceptions;

public class ValidationEmailDuplicated extends RuntimeException {
    public ValidationEmailDuplicated(String s) {
        super(s);
    }
}
