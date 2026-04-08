package ru.ship.ShipHub.models.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    @NotEmpty
    @NotNull
    public String email;
    @NotEmpty
    @NotNull
    @Size(min = 5, max = 100, message = "Пароль должен быть от 5 до 100 символов")
    public String password;
}
