package ru.berezentseva.deal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler  //для перехвата исключений, не описанных в других обработчиках
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse er = new ErrorResponse("Ошибка", e.getMessage());
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleStatementException(StatementException e) {
        ErrorResponse er = new ErrorResponse("Ошибка", e.getMessage());
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }
}
