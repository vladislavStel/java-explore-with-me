package ru.practicum.ewm.main_service.exception.error;

public class IncorrectlyRequestException extends RuntimeException {

    public IncorrectlyRequestException(final String message) {
        super(message);
    }

}