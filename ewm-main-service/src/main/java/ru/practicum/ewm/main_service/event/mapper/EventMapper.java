package ru.practicum.ewm.main_service.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.main_service.category.dto.CategoryDto;
import ru.practicum.ewm.main_service.category.model.Category;
import ru.practicum.ewm.main_service.event.dto.EventFullDto;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;
import ru.practicum.ewm.main_service.event.dto.NewEventDto;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.location.dto.LocationDto;
import ru.practicum.ewm.main_service.location.model.Location;
import ru.practicum.ewm.main_service.user.dto.UserShortDto;
import ru.practicum.ewm.main_service.user.model.User;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static Event toEvent(User user, Category category, Location location, NewEventDto newEventDto) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(category)
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(location)
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        LocationDto locationDto = LocationDto.builder()
                .lat(event.getLocation().getLat())
                .lon(event.getLocation().getLon())
                .build();
        UserShortDto userShortDto = UserShortDto.builder()
                .id(event.getInitiator().getId())
                .name(event.getInitiator().getName())
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(event.getCategory().getId())
                .name(event.getCategory().getName())
                .build();
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(locationDto)
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .initiator(userShortDto)
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        UserShortDto userShortDto = UserShortDto.builder()
                .id(event.getInitiator().getId())
                .name(event.getInitiator().getName())
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(event.getCategory().getId())
                .name(event.getCategory().getName())
                .build();
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .initiator(userShortDto)
                .eventDate(event.getEventDate())
                .paid(event.isPaid())
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .build();
    }

}