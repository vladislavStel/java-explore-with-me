package ru.practicum.ewm.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main_service.event.enums.StateAction;
import ru.practicum.ewm.main_service.location.dto.LocationDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class UpdateEvent {

    @Size(min = 3, max = 120)
    String title;

    @Size(min = 20, max = 2000)
    String annotation;

    @Size(min = 20, max = 7000)
    String description;

    @FutureOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    Integer category;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    StateAction stateAction;

}