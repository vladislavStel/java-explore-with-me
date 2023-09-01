package ru.practicum.ewm.main_service.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserShortDto {

    long id;
    String name;

}