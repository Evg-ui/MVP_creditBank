package ru.berezentseva.deal.exception;

public class StatementException extends Exception {
    public StatementException(String message) {
        super(message);
    }

    public StatementException(String message, Throwable ex) {
        super(message, ex);
    }
}
