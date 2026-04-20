package ru.ship.ShipHub.models.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import ru.ship.ShipHub.util.PersonType;

public class PersonDTO {

    @Null
    private Integer id;

    @NotEmpty
    @Size(min = 2, max = 255, message = "Имя должно быть длинной от 2 до 255 символов")
    private String username;

    @NotEmpty
    @Size(min = 3, max = 255, message = "Почта должна быть длинной от 2 до 255 символов")
    private String email;

    @NotEmpty
    private PersonType type;

    private LegalInfoDTO legalInfo;

    private PhysicalInfoDTO physicalInfo;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PersonDTO(Integer id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public PersonDTO(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PersonType getType() {
        return type;
    }

    public void setType(PersonType type) {
        this.type = type;
    }

    public LegalInfoDTO getLegalInfo() {
        return legalInfo;
    }

    public void setLegalInfo(LegalInfoDTO legalInfo) {
        this.legalInfo = legalInfo;
    }

    public PhysicalInfoDTO getPhysicalInfo() {
        return physicalInfo;
    }

    public void setPhysicalInfo(PhysicalInfoDTO physicalInfo) {
        this.physicalInfo = physicalInfo;
    }
}
