package ru.ship.ShipHub.models.dto;

public class PhysicalInfoDTO {

    private String address;

    public PhysicalInfoDTO(){}

    public PhysicalInfoDTO(String address){
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
