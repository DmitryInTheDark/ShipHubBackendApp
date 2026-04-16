package ru.ship.ShipHub.util.exceptions;

public class BaseException extends RuntimeException{

    public String message;

    public BaseException(String message){ super(message); }
}

