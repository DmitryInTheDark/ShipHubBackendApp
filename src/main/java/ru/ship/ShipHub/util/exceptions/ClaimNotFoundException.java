package ru.ship.ShipHub.util.exceptions;

public class ClaimNotFoundException extends RuntimeException {
  public ClaimNotFoundException(){super("Заявка не найдена");}
    public ClaimNotFoundException(String message) {
        super(message);
    }
}
