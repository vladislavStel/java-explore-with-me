package ru.practicum.ewm.main_service.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CompilationDto {

    Long id;
    boolean pinned;
    String title;
    Set<EventShortDto> events = new HashSet<>();

}