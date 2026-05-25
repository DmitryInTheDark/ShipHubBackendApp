package ru.ship.ShipHub.models.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {

    @NotEmpty
    @Size(min = 2, max = 255, message = "Имя должно быть длинной от 2 до 255 символов")
    private String username;

    @NotEmpty
    @Size(min = 3, max = 255, message = "Почта должна быть длинной от 2 до 255 символов")
    private String email;

    private String password;

    private Boolean isActive;

    private String verificationCode;

    private LegalInfoDTO legalInfo;

    private PhysicalInfoDTO physicalInfo;

    public UserUpdateDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
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
