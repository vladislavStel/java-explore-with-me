package ru.practicum.ewm.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main_service.category.dto.CategoryDto;
import ru.practicum.ewm.main_service.event.enums.EventState;
import ru.practicum.ewm.main_service.location.dto.LocationDto;
import ru.practicum.ewm.main_service.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    long id;
    String title;
    String annotation;
    String description;
    UserShortDto initiator;
    CategoryDto category;
    LocationDto location;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    EventState state;
    boolean paid;
    int participantLimit;
    Boolean requestModeration;
    long confirmedRequests;
    long views;

}