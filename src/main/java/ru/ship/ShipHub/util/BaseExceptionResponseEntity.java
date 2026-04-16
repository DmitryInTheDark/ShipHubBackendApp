package ru.ship.ShipHub.util;

public class BaseExceptionResponseEntity {

    public String message;

    public int code;

    public BaseExceptionResponseEntity(String message, int code){
        this.message = message;
        this.code = code;
    }
}
