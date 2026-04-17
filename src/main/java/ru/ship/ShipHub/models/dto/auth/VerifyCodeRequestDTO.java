package ru.ship.ShipHub.models.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class VerifyCodeRequestDTO {
    @NotEmpty
//    @Size(min = 5, max = 5, message = "Код должен быть длинной пять символов")
    public String code;

    @NotEmpty
    @Size(min = 3, max = 256, message = "Адрес почты должен быть длинной от 3 до 256 символов")
    public String email;
}
