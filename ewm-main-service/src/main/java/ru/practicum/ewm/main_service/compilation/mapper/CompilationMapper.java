package ru.practicum.ewm.main_service.compilation.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.main_service.compilation.dto.CompilationDto;
import ru.practicum.ewm.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main_service.compilation.model.Compilation;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;
import ru.practicum.ewm.main_service.event.mapper.EventMapper;
import ru.practicum.ewm.main_service.event.model.Event;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .pinned(newCompilationDto.isPinned())
                .title(newCompilationDto.getTitle())
                .events(events)
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation, Set<Event> events) {
        Set<EventShortDto> eventShortDtoList = events.stream().map(EventMapper::toEventShortDto)
                .collect(Collectors.toSet());
        CompilationDto compilationDto = CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .build();
        compilationDto.getEvents().addAll(eventShortDtoList);
        return compilationDto;
    }

    public static List<CompilationDto> toCompilationDtoList(List<Compilation> compilations,
                                                            Set<Event> events) {
        Map<Long, EventShortDto> eventDtoByEventIds = events.stream().map(EventMapper::toEventShortDto)
                .collect(Collectors.toMap(EventShortDto::getId, Function.identity()));


        return compilations.stream()
                .map(compilation -> {
                    CompilationDto compilationDto = toEmptyCompilationDto(compilation);
                    Set<EventShortDto> eventShortDtoForCompilation =
                            compilation.getEvents().stream()
                                    .map(event -> eventDtoByEventIds.get(event.getId())).collect(Collectors.toSet());
                    compilationDto.getEvents().addAll(eventShortDtoForCompilation);
                    return compilationDto;
                })
                .collect(Collectors.toList());
    }

    public static CompilationDto toEmptyCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .build();
        compilationDto.getEvents().addAll(Collections.emptySet());
        return compilationDto;
    }

}