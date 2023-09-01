package ru.practicum.ewm.main_service.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main_service.compilation.repository.CompilationRepository;
import ru.practicum.ewm.main_service.compilation.dto.CompilationDto;
import ru.practicum.ewm.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main_service.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.main_service.compilation.model.Compilation;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.service.EventService;
import ru.practicum.ewm.main_service.exception.error.ObjectAlreadyExistException;
import ru.practicum.ewm.main_service.exception.error.ObjectNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> findCompilationsEvents(Boolean pinned, int from, int size) {
        Pageable page = PageRequest.of(from, size);
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(page).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, page);
        }
        if (compilations.isEmpty()) {
            return Collections.emptyList();
        }
        return makeCompilationDtoList(compilations);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto findCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Compilation not found: id=%d", compId)));
        List<Long> listIds = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
        Set<Event> events = eventService.findEventByIds(listIds);
        return CompilationMapper.toCompilationDto(compilation, events);
    }

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        try {
            log.info("Save compilation in the repository, compilation={}", newCompilationDto);
            if (newCompilationDto.getEvents().isEmpty()) {
                return CompilationMapper.toEmptyCompilationDto(compilationRepository.save(
                        CompilationMapper.toCompilation(newCompilationDto, Collections.emptySet())));
            } else {
                Set<Event> events = eventService.findEventByIds(newCompilationDto.getEvents());
                return CompilationMapper.toCompilationDto(compilationRepository.save(
                        CompilationMapper.toCompilation(newCompilationDto, events)), events);
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Compilation with name is already registered, name={}", newCompilationDto.getTitle());
            throw new ObjectAlreadyExistException("Compilation with name " +
                    newCompilationDto.getTitle() + " is already registered.");
        }
    }

    @Override
    public void deleteCompilationById(long compId) {
        if (!compilationRepository.existsById(compId)) {
            log.error("Compilation not found, id={}", compId);
            throw new ObjectNotFoundException(String.format("Compilation not found: id=%d", compId));
        } else {
            compilationRepository.deleteById(compId);
        }
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Set<Event> eventShortDtoList = new HashSet<>();
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Compilation not found: id=%d", compId)));
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            eventShortDtoList = eventService.findEventByIds(updateCompilationRequest.getEvents());
            compilation.setEvents(eventShortDtoList);
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), eventShortDtoList);
    }

    private List<Long> getEventIdsByCompilations(List<Compilation> compilations) {
        return compilations.stream()
                .map(Compilation::getEvents)
                .flatMap(Set::stream)
                .map(Event::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<CompilationDto> makeCompilationDtoList(List<Compilation> compilations) {
        List<Long> eventIds = getEventIdsByCompilations(compilations);
        if (eventIds.isEmpty()) {
            return compilations.stream().map(CompilationMapper::toEmptyCompilationDto).collect(Collectors.toList());
        }
        Set<Event> events = eventService.findEventByIds(eventIds);
        return CompilationMapper.toCompilationDtoList(compilations, events);
    }

}