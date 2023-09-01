package ru.practicum.ewm.main_service.request.service;

import ru.practicum.ewm.main_service.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> findAllRequestsByUserId(long userId);

    ParticipationRequestDto saveRequest(long userId, long eventId);

    ParticipationRequestDto patchRequest(long userId, long requestId);

}