package ru.practicum.ewm.main_service.location.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDto {

    float lat;
    float lon;

}