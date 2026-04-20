package ru.ship.ShipHub.util.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super("Неправильные входные данные");
    }
    public BadRequestException(String message) {
        super(message);
    }
}
