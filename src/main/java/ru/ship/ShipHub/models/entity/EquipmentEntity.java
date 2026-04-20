package ru.ship.ShipHub.models.entity;

import jakarta.persistence.*;
import ru.ship.ShipHub.util.EquipmentType;

import java.util.List;

@Entity
@Table(name = "equipments")
public class EquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipment_type")
    @Enumerated(value = EnumType.STRING)
    private EquipmentType equipmentType;

    @Column(name = "name")
    private String name;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "serial_number")
    private String serialNumber;

    @OneToMany(
            targetEntity = EquipmentImageEntity.class,
            mappedBy = "equipment"
    )
    private List<EquipmentImageEntity> images;

    @Column(name = "count")
    private int count;

    @Column(name = "is_custom_type")
    private boolean isCustomType;

    @Column(name = "custom_type")
    private String customType;

    @OneToOne(
            mappedBy = "equipment"
    )
    private ClaimEntity claim;

    public EquipmentEntity() {}

    public EquipmentEntity(
            EquipmentType equipmentType,
            String name,
            String manufacturer,
            String serialNumber,
            List<EquipmentImageEntity> images,
            int count,
            boolean isCustomType,
            String customType,
            ClaimEntity claim
    ) {
        this.equipmentType = equipmentType;
        this.name = name;
        this.manufacturer = manufacturer;
        this.serialNumber = serialNumber;
        this.images = images;
        this.count = count;
        this.isCustomType = isCustomType;
        this.customType = customType;
        this.claim = claim;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public List<EquipmentImageEntity> getImages() {
        return images;
    }

    public void setImages(List<EquipmentImageEntity> images) {
        this.images = images;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isCustomType() {
        return isCustomType;
    }

    public void setCustomType(boolean customType) {
        isCustomType = customType;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public ClaimEntity getClaim() {
        return claim;
    }

    public void setClaim(ClaimEntity claim) {
        this.claim = claim;
    }
}
