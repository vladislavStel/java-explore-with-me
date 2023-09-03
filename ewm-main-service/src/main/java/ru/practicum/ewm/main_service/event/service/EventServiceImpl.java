package ru.practicum.ewm.main_service.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common_dto.ViewStatDto;
import ru.practicum.ewm.main_service.category.model.Category;
import ru.practicum.ewm.main_service.category.service.CategoryService;
import ru.practicum.ewm.main_service.event.dto.*;
import ru.practicum.ewm.main_service.event.enums.EventSort;
import ru.practicum.ewm.main_service.event.enums.EventState;
import ru.practicum.ewm.main_service.event.enums.StateAction;
import ru.practicum.ewm.main_service.event.mapper.EventMapper;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.repository.EventRepository;
import ru.practicum.ewm.main_service.event.repository.RepositorySearchOnCriteria;
import ru.practicum.ewm.main_service.exception.error.*;
import ru.practicum.ewm.main_service.location.model.Location;
import ru.practicum.ewm.main_service.location.service.LocationService;
import ru.practicum.ewm.main_service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main_service.request.enums.RequestStatus;
import ru.practicum.ewm.main_service.request.mapper.RequestMapper;
import ru.practicum.ewm.main_service.request.model.Request;
import ru.practicum.ewm.main_service.request.repository.RequestRepository;
import ru.practicum.ewm.main_service.statistic.StatisticService;
import ru.practicum.ewm.main_service.user.model.User;
import ru.practicum.ewm.main_service.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final LocationService locationService;
    private final CategoryService categoryService;
    private final RequestRepository requestRepository;
    private final StatisticService statisticService;
    private final RepositorySearchOnCriteria criteria;
    private final EntityManager em;

    @Override
    public List<EventShortDto> findEventByUserId(long userId, int from, int size) {
        userService.validateUserById(userId);
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        return getEventShortDto(events);
    }

    @Override
    @Transactional
    public EventFullDto saveEvent(long userId, NewEventDto newEventDto) {
        User initiator = userService.findUserById(userId);
        Location location = locationService.saveLocation(newEventDto.getLocation());
        Category category = categoryService.getById(newEventDto.getCategory());
        Event event = EventMapper.toEvent(initiator, category, location, newEventDto);
        if (newEventDto.getRequestModeration() != null) {
            event.setRequestModeration(newEventDto.getRequestModeration());
        } else {
            event.setRequestModeration(true);
        }
        if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new InvalidRequestParameterException("Field: eventDate. Error: must contain a date that has not yet arrived. " +
                    "Value: " + event.getEventDate());
        }
        event.setState(EventState.PENDING);
        event.setInitiator(initiator);
        event.setLocation(location);
        event.setCategory(category);
        Event savedEvent = eventRepository.save(event);
        return getEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto findEventById(long userId, long eventId) {
        Event event = getEventById(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new ObjectNotFoundException(String.format("User not found: id=%d", userId));
        }
        return getEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto patchEventById(long userId, long eventId, UpdateEvent updateEvent) {
        User user = userService.findUserById(userId);
        Event event = addUpdatableFields(updateEvent, eventId);
        if (event.getState() == EventState.PUBLISHED) {
            throw new InvalidObjectStatusException("Event must not be published");
        }
        if (!Objects.equals(event.getInitiator(), user)) {
            throw new ObjectNotFoundException(String.format("User not found: id=%d", userId));
        }
        if (updateEvent.getStateAction() != null) {
            event.setState(getEventState(event, updateEvent.getStateAction()));
        }
        Event updatedEvent = eventRepository.save(event);
        return getEventFullDto(updatedEvent);
    }

    @Override
    public List<ParticipationRequestDto> findEventsRequests(long userId, long eventId) {
        Event event = getEventById(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new InvalidRequestParameterException(String.format("User with id %d is not the initiator of event with id %d",
                    userId, eventId));
        }
        List<Request> requestList = requestRepository.findAllByEventId(eventId);
        return requestList.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestUpdateResult patchEventRequest(long userId, long eventId, UpdateRequest updateRequest) {
        Event event = getEventById(eventId);
        RequestStatus status = RequestStatus.from(updateRequest.getStatus())
                .orElseThrow(() -> new IllegalArgumentException("Unknown status"));

        if (event.getInitiator().getId() != userId) {
            throw new InvalidRequestParameterException(String.format("User with id %d is not the initiator of event with id %d",
                    userId, eventId));
        }
        if (event.getParticipantLimit() == 0 && !event.getRequestModeration()) {
            throw new InvalidRequestParameterException(String.format("Event with id %d does not require requests to be approved",
                    event.getId()));
        }

        List<Request> requestList = requestRepository.findAllByIdIn(updateRequest.getRequestIds());

        requestList.forEach(request -> {
            if (request.getEvent().getId() != eventId) {
                log.error("Request with id {} does not belong to the event with id {}", request.getId(), eventId);
                throw new ObjectNotFoundException(String.format("Request with id %d does not belong to" +
                        " the event with id %d", request.getId(), eventId));
            }
            if (request.getStatus() != RequestStatus.PENDING) {
                log.error("Request must have status PENDING");
                throw new InvalidObjectStatusException("Request must have status PENDING");
            }
        });

        Integer numberConfirmedRequests = requestRepository.countRequestsByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);

        if (status == RequestStatus.CONFIRMED) {
            if (numberConfirmedRequests >= event.getParticipantLimit()) {
                throw new InvalidObjectStatusException("Event participant limit has ended");
            }
            int participantLimitRemainder = event.getParticipantLimit() - numberConfirmedRequests;
            if (participantLimitRemainder >= requestList.size()) {
                requestList.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                List<ParticipationRequestDto> updatedRequestList = requestRepository.saveAll(requestList).stream()
                        .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
                return RequestMapper.toEventRequestStatusUpdateResult(updatedRequestList, Collections.emptyList());
            } else {
                IntStream.range(0, participantLimitRemainder)
                        .forEach(i -> requestList.get(i).setStatus(RequestStatus.CONFIRMED));
                IntStream.range(participantLimitRemainder, requestList.size())
                        .forEach(i -> requestList.get(i).setStatus(RequestStatus.REJECTED));
                Stream<Request> confirmedRequests = requestList.stream().limit(participantLimitRemainder);
                Stream<Request> rejectedRequests = requestList.stream().skip(participantLimitRemainder);
                requestRepository.saveAll(Stream.concat(confirmedRequests, rejectedRequests).collect(Collectors.toList()));
                return RequestMapper.toEventRequestStatusUpdateResult(confirmedRequests
                                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()),
                        rejectedRequests.map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()));
            }
        } else if (status == RequestStatus.REJECTED) {
            requestList.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            List<ParticipationRequestDto> updatedRequestList = requestRepository.saveAll(requestList).stream()
                    .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
            return RequestMapper.toEventRequestStatusUpdateResult(Collections.emptyList(), updatedRequestList);
        } else {
            throw new IncorrectlyRequestException(String.format("Incorrectly status: %s",
                    updateRequest.getStatus()));
        }
    }

    @Override
    public List<EventFullDto> findAllEvents(SearchCriteriaObject searchCriteriaObject) {
        List<Event> result = criteria.searchAllEvents(searchCriteriaObject);
        if (result.isEmpty()) {
            return Collections.emptyList();
        } else {
            addConfirmedRequests(result);
            addViewsByStatisticService(result);
            return result.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public EventFullDto patchEventAdminRequest(long eventId, UpdateEvent updateEvent) {
        Event event = addUpdatableFields(updateEvent, eventId);
        if (!Objects.equals(event.getState(), EventState.PENDING)) {
            throw new InvalidObjectStatusException("Cannot publish the event because it's not in the right state: " + event.getState());
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction() == StateAction.PUBLISH_EVENT && event.getState() != EventState.PUBLISHED) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        Event updatedEvent = eventRepository.save(event);
        return getEventFullDto(updatedEvent);
    }

    @Override
    public List<EventShortDto> findEventsBySearchQueryAndSort(SearchCriteriaObject searchCriteriaObject,
                                                              HttpServletRequest request) {
        statisticService.addHit(request);
        List<Event> result = criteria.searchEventsByQueryAndSort(searchCriteriaObject);
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        addViewsByStatisticService(result);
        addConfirmedRequests(result);

        if (searchCriteriaObject.getOnlyAvailable() && searchCriteriaObject.getSort() == EventSort.VIEWS) {
            result = result.stream()
                    .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                    .sorted(Comparator.comparingLong(Event::getViews).reversed())
                    .collect(Collectors.toList());
        } else if (!searchCriteriaObject.getOnlyAvailable() && searchCriteriaObject.getSort() == EventSort.VIEWS) {
            result = result.stream()
                    .sorted(Comparator.comparingLong(Event::getViews).reversed())
                    .collect(Collectors.toList());
        }
        return result.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto findEventFullInfo(long id, HttpServletRequest request) {
        statisticService.addHit(request);
        Event event = getEventById(id);
        if (event.getState() != EventState.PUBLISHED) {
            log.error("Trying to view unpublished event id {}", id);
            throw new ObjectNotFoundException(String.format("Event with id %d has not been published yet", id));
        }
        return getEventFullDto(event);
    }

    @Override
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event not found: id=%d", eventId)));
    }

    @Override
    public Set<Event> findEventByIds(List<Long> listId) {
        return new HashSet<>(eventRepository.findAllById(listId));
    }

    private Event addUpdatableFields(UpdateEvent updateEvent, long eventId) {
        Event event = getEventById(eventId);
        if (updateEvent.getEventDate() != null && !updateEvent.getEventDate()
                .isAfter(event.getCreatedOn().plusHours(2))) {
            throw new InvalidRequestParameterException("Field: eventDate. Error: must contain a date that has not yet arrived. " +
                    "Value: " + updateEvent.getEventDate());
        }
        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            event.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getCategory() != null) {
            Category updateCategory = categoryService.getById(updateEvent.getCategory());
            event.setCategory(updateCategory);
        }
        if (updateEvent.getLocation() != null) {
            Location location = locationService.checkAvailabilityLocation(updateEvent.getLocation().getLat(),
                    updateEvent.getLocation().getLon());
            event.setLocation(location);
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        return event;
    }

    private EventState getEventState(Event event, StateAction stateAction) {
        switch (stateAction) {
            case CANCEL_REVIEW:
                return EventState.CANCELED;
            case PUBLISH_EVENT:
                return EventState.PUBLISHED;
            case SEND_TO_REVIEW:
                return EventState.PENDING;
            default:
                return event.getState();
        }
    }

    private EventFullDto getEventFullDto(Event event) {
        addConfirmedRequests(event);
        addViewsByStatisticService(event);
        return EventMapper.toEventFullDto(event);
    }

    private List<EventShortDto> getEventShortDto(List<Event> events) {
        addConfirmedRequests(events);
        addViewsByStatisticService(events);
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    private void addViewsByStatisticService(Event event) {
        LocalDateTime rangeStart = event.getCreatedOn();
        LocalDateTime rangeEnd = LocalDateTime.now();
        List<String> uris = List.of("/events/" + event.getId());

        Optional<ViewStatDto> viewStatDto = statisticService.getStatistic(rangeStart, rangeEnd, uris, true)
                .stream().findFirst();
        event.setViews(viewStatDto.map(ViewStatDto::getHits).orElse(0L));
    }

    private void addViewsByStatisticService(List<Event> events) {
        LocalDateTime rangeStart = events.stream().map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo).orElse(LocalDateTime.now().minusHours(2));
        List<String> uris = events.stream().map(Event::getId).map(i -> "/events/" + i).collect(Collectors.toList());

        Map<String, ViewStatDto> statDtoMap = statisticService.getStatistic(rangeStart,
                LocalDateTime.now(), uris, true)
                .stream().collect(Collectors.toMap(ViewStatDto::getUri, viewStatDto -> viewStatDto,
                        (oldValue, newValue) -> oldValue));
        events.forEach(event -> {
            String keyEvent = "/events/" + event.getId();
            if (statDtoMap.containsKey(keyEvent)) {
                event.setViews(statDtoMap.get(keyEvent).getHits());
            } else {
                event.setViews(0);
            }
        });
    }

    private void addConfirmedRequests(Event event) {
        event.setConfirmedRequests(requestRepository.countRequestsByEventIdAndStatus(event.getId(),
                RequestStatus.CONFIRMED));
    }

    private void addConfirmedRequests(List<Event> events) {
        Set<Long> eventIds = events
                .stream()
                .map(Event::getId)
                .collect(Collectors.toSet());
        Map<Long, Long> resultMap = em.createQuery(
                "select request.event.id as eventId, COUNT(request.id) as requests " +
                "from Request request " +
                "where status = 'CONFIRMED' and request.event.id in :eventIds " +
                "group by request.event.id", Tuple.class)
                .setParameter("eventIds", eventIds)
                .getResultStream()
                .collect(Collectors.toMap(
                        tuple -> ((Number) tuple.get("eventId")).longValue(),
                        tuple -> ((Number) tuple.get("requests")).longValue()
                ));
        events.forEach(event -> event.setConfirmedRequests(resultMap.getOrDefault(event.getId(), 0L)));
    }

}