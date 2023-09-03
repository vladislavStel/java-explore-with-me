package ru.practicum.ewm.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main_service.event.dto.EventFullDto;
import ru.practicum.ewm.main_service.event.dto.SearchCriteriaObject;
import ru.practicum.ewm.main_service.event.dto.UpdateEvent;
import ru.practicum.ewm.main_service.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAdminAllEvents(@Valid SearchCriteriaObject searchCriteriaObject) {
        log.info("MainService: Get Admin all events");
        return eventService.findAllEvents(searchCriteriaObject);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventAdminRequest(@PathVariable("eventId") long eventId,
                                               @Valid @RequestBody UpdateEvent updateEvent) {
        log.info("MainService: Admin patch event request, eventId={}, updateEventAdminRequest={}",
                eventId, updateEvent);
        return eventService.patchEventAdminRequest(eventId, updateEvent);
    }

}