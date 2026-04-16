package ru.ship.ShipHub.util.exceptions;

public class PersonIsExistException extends BaseException {
    public PersonIsExistException(String message) { super(message); }
    public PersonIsExistException() {super("Такой пользователь уже существует");}
}
