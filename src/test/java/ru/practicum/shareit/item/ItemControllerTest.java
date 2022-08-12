package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private ItemDto itemDto;
    private ItemDtoWithBooking itemDtoWithBooking;
    private CommentDto commentDto;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        itemDto = new ItemDto(
                1L,
                "Отвертка",
                "Позволяет что-то закрутить",
                true,
                1L,
                null
        );
        itemDtoWithBooking = new ItemDtoWithBooking(
                1L,
                "Отвертка",
                "Позволяет что-то закрутить",
                true,
                1L,
                null,
                null,
                null
        );
        commentDto = new CommentDto(
                1L,
                "Первый комментарий",
                1L,
                1L,
                LocalDateTime.of(2022, 8, 10, 11, 11),
                "Иван"
        );

    }

    @Test
    @SneakyThrows
    void saveNewItem() {
        when(itemService.addNewItem(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    @SneakyThrows
    void updateItem() {
        when(itemService.updateItem(anyLong(), any()))
                .thenReturn(Optional.of(itemDto));

        mvc.perform(put("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    @SneakyThrows
    void patchedItem() {
        when(itemService.patchedItem(anyLong(), anyLong(), any()))
                .thenReturn(Optional.of(itemDto));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    @SneakyThrows
    void findItemById() {
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(Optional.of(itemDtoWithBooking));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBooking.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithBooking.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBooking.getAvailable())))
                .andExpect(jsonPath("$.request", is(itemDtoWithBooking.getRequest()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(itemDtoWithBooking.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDtoWithBooking.getNextBooking())));
    }

    @Test
    @SneakyThrows
    void findAllItemsOfUser() {
        when(itemService.getUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDtoWithBooking));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithBooking.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithBooking.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithBooking.getAvailable())))
                .andExpect(jsonPath("$[0].request", is(itemDtoWithBooking.getRequest()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking", is(itemDtoWithBooking.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemDtoWithBooking.getNextBooking())));
    }

    @Test
    @SneakyThrows
    void search() {
        when(itemService.search(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithBooking.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithBooking.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithBooking.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDtoWithBooking.getRequest()), Long.class))
                .andExpect(jsonPath("$[0].comments", is(itemDtoWithBooking.getComments())));
    }

    @Test
    @SneakyThrows
    void deleteItem() {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(itemService, Mockito.times(1))
                .deleteItem(1L, 1L);
    }

    @Test
    @SneakyThrows
    void createComment() {
        mapper.registerModule(new JavaTimeModule());
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorId", is(commentDto.getAuthorId()), Long.class))
                .andExpect(jsonPath("$.created[0]", is(commentDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(commentDto.getCreated().getMonth().getValue())))
                .andExpect(jsonPath("$.created[2]", is(commentDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$.created[3]", is(commentDto.getCreated().getHour())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}