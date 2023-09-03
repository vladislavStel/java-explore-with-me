package ru.practicum.ewm.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main_service.event.dto.EventFullDto;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;
import ru.practicum.ewm.main_service.event.dto.SearchCriteriaObject;
import ru.practicum.ewm.main_service.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsBySearchQueryAndSort(@Valid SearchCriteriaObject searchCriteriaObject,
                                                             HttpServletRequest request) {
        log.info("MainService: Get events by search query");
        return eventService.findEventsBySearchQueryAndSort(searchCriteriaObject, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventFullInfo(@PathVariable("id") long id, HttpServletRequest request) {
        log.info("MainService: Get events full info, id={}", id);
        return eventService.findEventFullInfo(id, request);
    }

}