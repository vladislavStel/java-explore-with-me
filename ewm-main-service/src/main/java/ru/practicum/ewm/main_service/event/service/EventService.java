package ru.practicum.ewm.main_service.event.service;

import ru.practicum.ewm.main_service.event.dto.*;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface EventService {

    List<EventShortDto> findEventByUserId(long userId, int from, int size);

    EventFullDto saveEvent(long userId, NewEventDto newEventDto);

    EventFullDto findEventById(long userId, long eventId);

    EventFullDto patchEventById(long userId, long eventId, UpdateEvent updateEvent);

    List<ParticipationRequestDto> findEventsRequests(long userId, long eventId);

    RequestUpdateResult patchEventRequest(long userId, long eventId, UpdateRequest updateRequest);

    List<EventFullDto> findAllEvents(SearchCriteriaObject searchCriteriaObject);

    EventFullDto patchEventAdminRequest(long eventId, UpdateEvent updateEvent);

    List<EventShortDto> findEventsBySearchQueryAndSort(SearchCriteriaObject searchCriteriaObject,
                                                       HttpServletRequest request);

    EventFullDto findEventFullInfo(long id, HttpServletRequest request);

    Event getEventById(Long eventId);

    Set<Event> findEventByIds(List<Long> listId);

}