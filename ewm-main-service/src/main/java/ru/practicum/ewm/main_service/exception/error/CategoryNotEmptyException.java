package ru.practicum.ewm.main_service.exception.error;

public class CategoryNotEmptyException extends RuntimeException {

    public CategoryNotEmptyException(String message) {
        super(message);
    }
}