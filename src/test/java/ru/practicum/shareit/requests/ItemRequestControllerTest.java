package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoWithResponses;

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
class ItemRequestControllerTest {
    @Mock
    private RequestService requestService;

    @InjectMocks
    private ItemRequestController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoWithResponses reqDtoWithResp;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mapper.registerModule(new JavaTimeModule());

        itemRequestDto = new ItemRequestDto(
                1L,
                "Нужна отвертка",
                LocalDateTime.of(2022, 8, 9, 11, 10)
        );
        reqDtoWithResp = new ItemRequestDtoWithResponses(
                1L,
                "Отвертка",
                LocalDateTime.of(2022, 8, 9, 11, 10),
                null
        );
    }

    @Test
    @SneakyThrows
    void createNewRequest() {
        when(requestService.addNewRequest(anyLong(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created[0]", is(itemRequestDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(itemRequestDto.getCreated().getMonth().getValue())))
                .andExpect(jsonPath("$.created[2]", is(itemRequestDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$.created[3]", is(itemRequestDto.getCreated().getHour())));
    }

    @Test
    @SneakyThrows
    void findAllItemRequests() {
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created[0]", is(itemRequestDto.getCreated().getYear())))
                .andExpect(jsonPath("$[0].created[1]", is(itemRequestDto.getCreated()
                        .getMonth().getValue())))
                .andExpect(jsonPath("$[0].created[2]", is(itemRequestDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$[0].created[3]", is(itemRequestDto.getCreated().getHour())));
    }

    @Test
    @SneakyThrows
    void findAllRequestsOfUser() {
        when(requestService.findRequestsByUser(anyLong()))
                .thenReturn(List.of(reqDtoWithResp));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(reqDtoWithResp.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(reqDtoWithResp.getDescription())))
                .andExpect(jsonPath("$[0].created[0]", is(reqDtoWithResp.getCreated().getYear())))
                .andExpect(jsonPath("$[0].created[1]", is(reqDtoWithResp.getCreated()
                        .getMonth().getValue())))
                .andExpect(jsonPath("$[0].created[2]", is(reqDtoWithResp.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$[0].created[3]", is(reqDtoWithResp.getCreated().getHour())))
                .andExpect(jsonPath("$[0].items", is(reqDtoWithResp.getItems())));
    }

    @Test
    @SneakyThrows
    void findRequestById() {
        when(requestService.findRequestById(anyLong(), anyLong()))
                .thenReturn(Optional.of(reqDtoWithResp));

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(reqDtoWithResp.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(reqDtoWithResp.getDescription())))
                .andExpect(jsonPath("$.created[0]", is(reqDtoWithResp.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(reqDtoWithResp.getCreated()
                        .getMonth().getValue())))
                .andExpect(jsonPath("$.created[2]", is(reqDtoWithResp.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$.created[3]", is(reqDtoWithResp.getCreated().getHour())))
                .andExpect(jsonPath("$.items", is(reqDtoWithResp.getItems())));
    }
}