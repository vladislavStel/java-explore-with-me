package ru.practicum.ewm.main_service.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main_service.event.enums.EventState;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.service.EventService;
import ru.practicum.ewm.main_service.exception.error.InvalidObjectStatusException;
import ru.practicum.ewm.main_service.exception.error.ObjectAlreadyExistException;
import ru.practicum.ewm.main_service.exception.error.ObjectNotFoundException;
import ru.practicum.ewm.main_service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main_service.request.enums.RequestStatus;
import ru.practicum.ewm.main_service.request.mapper.RequestMapper;
import ru.practicum.ewm.main_service.request.model.Request;
import ru.practicum.ewm.main_service.request.repository.RequestRepository;
import ru.practicum.ewm.main_service.user.model.User;
import ru.practicum.ewm.main_service.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findAllRequestsByUserId(long userId) {
        userService.validateUserById(userId);
        List<Request> requestList = requestRepository.findAllByRequesterId(userId);
        return requestList.stream().map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto saveRequest(long userId, long eventId) {
        User requester = userService.findUserById(userId);
        Event event = eventService.getEventById(eventId);
        if (!Objects.equals(event.getState(), EventState.PUBLISHED)) {
            throw new InvalidObjectStatusException("Cannot create request, event not published, eventId: " + eventId);
        }
        if (Objects.equals(event.getInitiator(), requester)) {
            throw new InvalidObjectStatusException("You cannot create a request in your event, eventId: " + eventId);
        }
        Request request = Request.builder()
                .requester(requester)
                .event(event)
                .status(RequestStatus.PENDING)
                .build();
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        Integer countRequests = requestRepository.countRequestsByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && countRequests >= event.getParticipantLimit()) {
            throw new InvalidObjectStatusException("Event participant limit has ended");
        }
        try {
            log.info("Save request in the repository, request={}", request);
            return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
        } catch (DataIntegrityViolationException e) {
            log.error("Request for event by user already registered, eventId={}, userId={}", eventId, userId);
            throw new ObjectAlreadyExistException("Request for event= " + eventId + " by user= " +
                    userId + " already registered.");
        }
    }

    @Override
    public ParticipationRequestDto patchRequest(long userId, long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Request not found: id=%d", requestId)));
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new InvalidObjectStatusException(String.format("Editing request with id %d is not available" +
                    " for user with id %d", requestId, userId));
        }
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

}