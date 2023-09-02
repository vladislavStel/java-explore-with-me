package ru.practicum.ewm.main_service.event.service;

import ru.practicum.ewm.main_service.event.dto.*;
import ru.practicum.ewm.main_service.event.enums.EventSort;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {

    List<EventShortDto> findEventByUserId(long userId, int from, int size);

    EventFullDto saveEvent(long userId, NewEventDto newEventDto);

    EventFullDto findEventById(long userId, long eventId);

    EventFullDto patchEventById(long userId, long eventId, UpdateEvent updateEvent);

    List<ParticipationRequestDto> findEventsRequests(long userId, long eventId);

    RequestUpdateResult patchEventRequest(long userId, long eventId, UpdateRequest updateRequest);

    List<EventFullDto> findAllEvents(List<Long> users, List<String> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto patchEventAdminRequest(long eventId, UpdateEvent updateEvent);

    List<EventShortDto> findEventsBySearchQueryAndSort(String text, List<Long> categories, Boolean paid,
                                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                       Boolean onlyAvailable, EventSort sort, int from, int size,
                                                       HttpServletRequest request);

    EventFullDto findEventFullInfo(long id, HttpServletRequest request);

    Event getEventById(Long eventId);

    Set<Event> findEventByIds(List<Long> listId);

}