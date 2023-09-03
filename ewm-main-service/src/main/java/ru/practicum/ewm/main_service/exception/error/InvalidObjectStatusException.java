package ru.practicum.ewm.main_service.exception.error;

public class InvalidObjectStatusException extends RuntimeException {

    public InvalidObjectStatusException(String message) {
        super(message);
    }

}