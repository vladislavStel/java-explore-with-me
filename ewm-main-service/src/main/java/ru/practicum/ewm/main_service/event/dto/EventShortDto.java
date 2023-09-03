package ru.practicum.ewm.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main_service.category.dto.CategoryDto;
import ru.practicum.ewm.main_service.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class EventShortDto {

    Long id;
    String title;
    String annotation;
    UserShortDto initiator;
    CategoryDto category;
    long confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Boolean paid;
    long views;

}