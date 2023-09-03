package ru.practicum.ewm.main_service.event.enums;

import java.util.Optional;

public enum EventState {

    PENDING, PUBLISHED, CANCELED;

    public static Optional<EventState> from(String stringStatus) {
        for (EventState status : values()) {
            if (status.name().equalsIgnoreCase(stringStatus)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }

}