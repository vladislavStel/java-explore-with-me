package ru.practicum.ewm.main_service.request.enums;

import java.util.Optional;

public enum RequestStatus {

    PENDING, CONFIRMED, REJECTED, CANCELED;

    public static Optional<RequestStatus> from(String stringStatus) {
        for (RequestStatus status : values()) {
            if (status.name().equalsIgnoreCase(stringStatus)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }

}