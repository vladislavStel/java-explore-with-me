package ru.practicum.ewm.main_service.exception.error;

public class InvalidRequestParameterException extends RuntimeException {

    public InvalidRequestParameterException(String message) {
        super(message);
    }

}