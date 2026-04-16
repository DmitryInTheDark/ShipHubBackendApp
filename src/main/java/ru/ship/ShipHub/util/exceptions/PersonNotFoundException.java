package ru.ship.ShipHub.util.exceptions;

public class PersonNotFoundException extends RuntimeException{

    public PersonNotFoundException(String message){ super(message); }
    public PersonNotFoundException(){ super("Пользователь не найден"); }
}
