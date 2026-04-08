package ru.ship.ShipHub.util.exceptions;

public class PersonIsExistException extends RuntimeException {
    public PersonIsExistException(String message) {
        super(message);
    }
    public PersonIsExistException() {super("Такой пользователь уже существует");}
}
