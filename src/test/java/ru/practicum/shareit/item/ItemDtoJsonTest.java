package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = new ItemDto(
                1L,
                "Отвертка",
                "Позволяет что-то закрутить",
                true,
                1L,
                null
        );
        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Отвертка");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Позволяет что-то закрутить");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}
