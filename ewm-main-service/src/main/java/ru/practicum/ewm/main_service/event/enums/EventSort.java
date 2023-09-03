package ru.practicum.ewm.main_service.event.enums;

import java.util.Optional;

public enum EventSort {

    EVENT_DATE, VIEWS;

    public static Optional<EventSort> from(String stringSort) {
        for (EventSort sort : values()) {
            if (sort.name().equalsIgnoreCase(stringSort)) {
                return Optional.of(sort);
            }
        }
        return Optional.empty();
    }

}