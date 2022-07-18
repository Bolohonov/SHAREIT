//package ru.practicum.shareit.booking.dto;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.JsonSerializer;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import lombok.SneakyThrows;
//
//import java.io.IOException;
//
//public class BookingDtoSerializer extends JsonSerializer<BookingDto> {
//
//    private BookingDto.Item item;
//
//    @SneakyThrows
//    @Override
//    public void serialize(BookingDto bookingDto,
//                          JsonGenerator jgen,
//                          SerializerProvider serializerProvider) throws IOException {
//
//        jgen.writeStartObject();
//        BookingDto.Item item = bookingDto.getItem();
//        BookingDto.Booker booker = bookingDto.getBooker();
//        Long bookerId = booker.
//        jgen.writeNumberField("id", bookingDto.getId());
//        jgen.writeObjectField("start", bookingDto.getStart());
//        jgen.writeObjectField("end", bookingDto.getEnd());
//        jgen.writeObjectField("item", item);
//        jgen.writeObjectField("booker", bookingDto.getBooker().);
//        jgen.writeEndObject();
//    }
//}
