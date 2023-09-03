package ru.practicum.ewm.main_service.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.main_service.event.dto.RequestUpdateResult;
import ru.practicum.ewm.main_service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main_service.request.model.Request;

import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .status(request.getStatus()).build();
    }

    public static RequestUpdateResult toEventRequestStatusUpdateResult(List<ParticipationRequestDto> confirms,
                                                                       List<ParticipationRequestDto> rejects) {
        return RequestUpdateResult.builder()
                .confirmedRequests(confirms)
                .rejectedRequests(rejects)
                .build();
    }

}