package ru.practicum.ewm.main_service.exception.error;

public class ValidationException extends RuntimeException {

    public ValidationException(final String message) {
        super(message);
    }

}