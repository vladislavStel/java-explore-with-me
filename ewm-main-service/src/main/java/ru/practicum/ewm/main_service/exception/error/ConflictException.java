package ru.practicum.ewm.main_service.exception.error;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

}