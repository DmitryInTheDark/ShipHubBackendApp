package ru.ship.ShipHub.models.dto.auth;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.ship.ShipHub.models.dto.LegalInfoDTO;
import ru.ship.ShipHub.models.dto.PhysicalInfoDTO;
import ru.ship.ShipHub.util.PersonType;

public class RegistrationRequestDTO {

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

    @Nullable
    public LegalInfoDTO legalInfo;

    @Nullable
    public PhysicalInfoDTO physicalInfo;
}
