package ru.practicum.ewm.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.event.dto.EventFullDto;
import ru.practicum.ewm.main_service.event.dto.UpdateEvent;
import ru.practicum.ewm.main_service.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAdminAllEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                                @RequestParam(name = "states", required = false) List<String> states,
                                                @RequestParam(name = "categories", required = false) List<Long> categories,
                                                @RequestParam(name = "rangeStart", required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(name = "rangeEnd", required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size, HttpServletRequest request) {
        log.info("MainService: Get Admin all events, " +
                "users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.findAllEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventAdminRequest(@PathVariable("eventId") long eventId,
                                               @Valid @RequestBody UpdateEvent updateEvent) {
        log.info("MainService: Admin patch event request, eventId={}, updateEventAdminRequest={}",
                eventId, updateEvent);
        return eventService.patchEventAdminRequest(eventId, updateEvent);
    }

}