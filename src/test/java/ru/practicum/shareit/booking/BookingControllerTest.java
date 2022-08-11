package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;

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
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mapper.registerModule(new JavaTimeModule());
        LocalDateTime start = LocalDateTime.parse("2022-08-01T11:50");
        LocalDateTime end = LocalDateTime.parse("2022-08-02T12:50");

        bookingDto = new BookingDto(
                1L,
                start,
                end,
                null,
                null,
                Status.APPROVED);
    }

    @Test
    @SneakyThrows
    void saveNewBooking() {
        when(bookingService.addNew(anyLong(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonth().getValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonth().getValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    @SneakyThrows
    void approveOrRejectBooking() {
        when(bookingService.approveOrRejectBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(Optional.of(bookingDto));

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(false))
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonth().getValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonth().getValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

    }

    @Test
    @SneakyThrows
    void findBookingById() {
        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(Optional.of(bookingDto));

        mvc.perform(get("/bookings/1")
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonth().getValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonth().getValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));


    }

    @Test
    @SneakyThrows
    void findBookingByUser() {
        when(bookingService.getUserBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$[0].start[1]", is(bookingDto.getStart().getMonth().getValue())))
                .andExpect(jsonPath("$[0].start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$[0].end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$[0].end[1]", is(bookingDto.getEnd().getMonth().getValue())))
                .andExpect(jsonPath("$[0].end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$[0].end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    @SneakyThrows
    void findBookingByOwner() {
        when(bookingService.getBookingsByOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$[0].start[1]", is(bookingDto.getStart().getMonth().getValue())))
                .andExpect(jsonPath("$[0].start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$[0].end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$[0].end[1]", is(bookingDto.getEnd().getMonth().getValue())))
                .andExpect(jsonPath("$[0].end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$[0].end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }
}