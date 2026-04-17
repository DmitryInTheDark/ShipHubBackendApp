package ru.ship.ShipHub.models.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.ship.ShipHub.util.PersonType;

public class RegistrationRequest {

    @NotEmpty
    public String email;

    @NotEmpty
    @Size(min = 5, max = 100, message = "Пароль должен быть от 5 до 100 символов")
    public String password;

    @NotEmpty
    @Size(min = 2, max = 100, message = "Имя должно быть длинной от 2 до 100 символов")
    public String name;

    @NotNull
    public PersonType type;
}
