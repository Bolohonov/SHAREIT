package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        BookingDto.Item item = new BookingDto.Item(1L, "Отвертка");
        BookingDto.Booker booker = new BookingDto.Booker(1L);
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2022, 8, 10, 10, 10),
                LocalDateTime.of(2022, 8, 11, 10, 10),
                item,
                booker,
                Status.WAITING
        );
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-08-10T10:10:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-08-11T10:10:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathValue("$.item", "id", "name")
                .extracting("id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item", "id", "name")
                .extracting("name").isEqualTo("Отвертка");
        assertThat(result).extractingJsonPathValue("$.booker", "id")
                .extracting("id").isEqualTo(1);
    }
}