package ru.practicum.shareit.booking.exceptions;

public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException(String s) {
        super(s);
    }
}
