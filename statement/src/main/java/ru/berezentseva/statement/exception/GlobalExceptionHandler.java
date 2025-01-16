package ru.berezentseva.statement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler  //для перехвата исключений, не описанных в других обработчиках
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse er = new ErrorResponse("Ошибка", e.getMessage());
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }
}
