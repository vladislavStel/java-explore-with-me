package ru.practicum.ewm.main_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.main_service.exception.error.*;
import ru.practicum.ewm.main_service.exception.model.ErrorResponse;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundExceptions(Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InvalidRequestParameterException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleInvalidRequestParameterException(Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler({InvalidObjectStatusException.class,
            CategoryNotEmptyException.class, ObjectAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse(HttpStatus.BAD_REQUEST,
                        error.getField(),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> handleConstraintViolationExceptions(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return e.getConstraintViolations().stream()
                .map(violation -> new ErrorResponse(HttpStatus.BAD_REQUEST,
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .collect(Collectors.toList());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, IncorrectlyRequestException.class,
            IllegalArgumentException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectlyMadeRequest(Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Exception e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}