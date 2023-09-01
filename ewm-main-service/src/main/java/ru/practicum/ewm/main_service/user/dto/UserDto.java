package ru.practicum.ewm.main_service.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    long id;
    String email;
    String name;

}