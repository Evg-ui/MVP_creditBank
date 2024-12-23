package ru.berezentseva.deal.exception;

import lombok.Data;

@Data
public class ErrorResponse {

    private String message;
    private String details;

    // будем перехватывать ошибки, которые не выбрасываются в других исключениях контроллера
    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }
}
