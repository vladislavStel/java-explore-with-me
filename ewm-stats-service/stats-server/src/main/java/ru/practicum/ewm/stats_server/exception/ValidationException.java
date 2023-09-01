package ru.practicum.ewm.stats_server.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(final String message) {
        super(message);
    }

}