package ru.ship.ShipHub.models.dto.claim;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import ru.ship.ShipHub.util.EquipmentType;

import java.util.ArrayList;
import java.util.List;

public class EquipmentDTO {

    @Null
    private Long id;

    private EquipmentType equipmentType;

    @NotEmpty
    private String name;

    @NotEmpty
    private String manufacturer;

    @NotEmpty
    private String serialNumber;

    @NotNull
    @Positive
    private int count;

    @Null
    private List<Long> imageIds = new ArrayList<>();

    private boolean isCustomType;

    private String customType;

    public EquipmentDTO(){}

    public EquipmentDTO(
            Long id,
            EquipmentType equipmentType,
            String name, String manufacturer,
            String serialNumber,
            int count,
            String customType,
            boolean isCustomType,
            List<Long> imageIds
    ) {
        this.id = id;
        this.equipmentType = equipmentType;
        this.name = name;
        this.manufacturer = manufacturer;
        this.serialNumber = serialNumber;
        this.count = count;
        this.customType = customType;
        this.isCustomType = isCustomType;
        this.imageIds = imageIds;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public boolean isCustomType() {
        return isCustomType;
    }

    public void setCustomType(boolean customType) {
        isCustomType = customType;
    }

    public List<Long> getImageIds() {
        return imageIds;
    }

    public void setImageIds(List<Long> imageIds) {
        this.imageIds = imageIds;
    }
}
