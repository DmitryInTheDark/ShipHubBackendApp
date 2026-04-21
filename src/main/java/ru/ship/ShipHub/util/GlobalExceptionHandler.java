package ru.ship.ShipHub.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.ship.ShipHub.util.exceptions.PersonIsExistException;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<BaseExceptionResponseEntity> handleException(Exception e){
        log.error("UnsupportedExceprion: ", e);
        return response(
                500,
                "Ошибка " + e.getLocalizedMessage(),
                0
        );
    }

    @ExceptionHandler
    public ResponseEntity<BaseExceptionResponseEntity> handlePersonIsExistException(PersonIsExistException e){
        return response(
                400,
                e.getMessage(),
                1
        );
    }

    @ExceptionHandler
    public ResponseEntity<BaseExceptionResponseEntity> handleBadCredentialsException(BadCredentialsException e){
        return response(
                400,
                e.getMessage(),
                1
        );
    }

    public ResponseEntity<BaseExceptionResponseEntity> response(Integer status, String message, int code){
        return ResponseEntity.status(Objects.requireNonNullElse(status, 500)).body(new BaseExceptionResponseEntity(message, code));
    }
}
