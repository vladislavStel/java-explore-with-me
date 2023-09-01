package ru.practicum.ewm.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.event.dto.*;
import ru.practicum.ewm.main_service.event.service.EventService;
import ru.practicum.ewm.main_service.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable("userId") long userId,
                                         @RequestParam(defaultValue = "0", required = false) int from,
                                         @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("MainService: Get events, userId={}, from={}, size={}", userId, from, size);
        return eventService.findEventByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("MainService: Create event, userId={}, newEventDto={}", userId, newEventDto);
        return eventService.saveEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable("userId") long userId,
                                     @PathVariable("eventId") long eventId) {
        log.info("MainService: Get event, userId={}, eventId={}", userId, eventId);
        return eventService.findEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventById(@PathVariable("userId") long userId,
                                       @PathVariable("eventId") long eventId,
                                       @Valid @RequestBody UpdateEvent updateEvent) {
        log.info("MainService: Patch event, userId={}, eventId={}, updateEventUserRequest={}",
                userId, eventId, updateEvent);
        return eventService.patchEventById(userId, eventId, updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventsRequests(@PathVariable("userId") long userId,
                                                           @PathVariable("eventId") long eventId) {
        log.info("MainService: Get events requests, userId={}, eventId={}", userId, eventId);
        return eventService.findEventsRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestUpdateResult patchEventRequest(@PathVariable("userId") long userId,
                                                 @PathVariable("eventId") long eventId,
                                                 @Valid @RequestBody UpdateRequest updateRequest) {
        log.info("MainService: Patch event request, userId={}, eventId={}, eventRequestStatusUpdateRequest={}",
                userId, eventId, updateRequest);
        return eventService.patchEventRequest(userId, eventId, updateRequest);
    }

}