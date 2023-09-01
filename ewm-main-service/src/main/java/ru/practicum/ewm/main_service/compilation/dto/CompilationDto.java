package ru.practicum.ewm.main_service.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;

import java.util.Set;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {

    Long id;
    boolean pinned;
    String title;
    Set<EventShortDto> events;

}