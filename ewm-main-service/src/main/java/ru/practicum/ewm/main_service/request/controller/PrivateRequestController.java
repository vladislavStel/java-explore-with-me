package ru.practicum.ewm.main_service.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main_service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main_service.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getAllRequestsByUserId(@PathVariable("userId") long userId) {
        log.info("MainService: Get all requests by userId, userId={}", userId);
        return requestService.findAllRequestsByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable("userId") long userId,
                                                 @RequestParam("eventId") long eventId) {
        log.info("MainService: Create request by userId and eventId, userId={}, eventId={}", userId, eventId);
        return requestService.saveRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto patchRequest(@PathVariable("userId") long userId,
                                                @PathVariable("requestId") long requestId) {
        log.info("MainService: Cancel request by userId and requestId, userId={}, requestId={}", userId, requestId);
        return requestService.patchRequest(userId, requestId);
    }

}