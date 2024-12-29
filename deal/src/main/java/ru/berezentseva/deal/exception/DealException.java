package ru.berezentseva.deal.exception;

public class DealException extends Exception {
    public DealException(String message) {
        super(message);
    }

    public DealException(String message, Throwable ex) {
        super(message, ex);
    }
}
