package ru.ship.ShipHub.models.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RestorePasswordEmailRequestDTO(
        @NotEmpty
        @Size(min = 3, max = 256, message = "Адрес почты должен быть длинной от 3 до 256 символов")
        String email
) {}
