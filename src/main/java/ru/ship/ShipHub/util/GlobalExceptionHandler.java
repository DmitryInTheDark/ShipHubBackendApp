package ru.ship.ShipHub.util;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.ship.ShipHub.util.exceptions.PersonIsExistException;
import ru.ship.ShipHub.util.exceptions.PersonNotFoundException;
import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        errors.put("Ошибка", "Некоректные данные");
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(400).body(errors);
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

    @ExceptionHandler
    public ResponseEntity<BaseExceptionResponseEntity> handleMailSendExceptionException(MailSendException e){
        return response(
                500,
                e.getMessage() != null ? e.getLocalizedMessage() : "Не удалось отправить письмо на почту, повторите попытку позже",
                1
        );
    }

    @ExceptionHandler
    public ResponseEntity<BaseExceptionResponseEntity> handlePersonNotFoundException(PersonNotFoundException e){
        return response(
                400,
                e.getMessage() != null ? e.getLocalizedMessage() : "Пользователь с такими данными не найден",
                1
        );
    }

    @ExceptionHandler
    public ResponseEntity<BaseExceptionResponseEntity> handleEntityNotFoundException(EntityNotFoundException e){
        return response(
                400,
                e.getMessage() != null ? e.getLocalizedMessage() : "сущность не найдена",
                1
        );
    }

    public ResponseEntity<BaseExceptionResponseEntity> response(Integer status, String message, int code){
        return ResponseEntity.status(Objects.requireNonNullElse(status, 500)).body(new BaseExceptionResponseEntity(message, code));
    }
}
