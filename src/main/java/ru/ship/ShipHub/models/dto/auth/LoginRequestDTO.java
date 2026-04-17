package ru.ship.ShipHub.models.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {
    @NotEmpty
    public String email;
    @NotEmpty
    @Size(min = 5, max = 100, message = "Пароль должен быть от 5 до 100 символов")
    public String password;
}
