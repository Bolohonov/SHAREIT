package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.exceptions.ItemValidationException;

import java.io.IOException;

public class ItemDeSerializer extends JsonDeserializer<Item> {

    @Override
    public Item deserialize(JsonParser jp, DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        JsonNode node = jp.getCodec().readTree(jp);
        String name;
        String description;
        Boolean av;
        try {
            name = node.get("name").asText();
            description = node.get("description").asText();
            av = node.get("available").asBoolean();
        } catch (NullPointerException e) {
            throw new ItemValidationException("Отсутствуют необходимые поля");
        }
        return Item.builder()
                .name(name)
                .description(description)
                .available(av)
                .build();
    }
}
