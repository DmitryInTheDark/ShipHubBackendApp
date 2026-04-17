package ru.ship.ShipHub.models.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class LegalInfoDTO {

    @NotEmpty
    @Size(min = 3, max = 256, message = "Некоректная длина имени организации")
    private String organizationName;

    @NotEmpty
    private String inn;

    @NotEmpty
    private String kpp;

    @NotEmpty
    private String address;

    @NotEmpty
    private String phone;

    public LegalInfoDTO(){}

    public LegalInfoDTO(String organizationName, String inn, String kpp, String address, String phone) {
        this.organizationName = organizationName;
        this.inn = inn;
        this.kpp = kpp;
        this.address = address;
        this.phone = phone;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
