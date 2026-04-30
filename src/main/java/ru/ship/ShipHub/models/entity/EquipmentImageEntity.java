package ru.ship.ShipHub.models.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "claim_images")
public class EquipmentImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] bytes;

    @ManyToOne
    @JoinColumn(name = "equipment_id", referencedColumnName = "id")
    private EquipmentEntity equipment;

    @Column(name = "description")
    private String description;

    @Column(name = "content_type")
    private String contentType;

    public EquipmentImageEntity() {}

    public EquipmentImageEntity(byte[] bytes, EquipmentEntity equipment, String description, String contentType) {
        this.bytes = bytes;
        this.equipment = equipment;
        this.description = description;
        this.contentType = contentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public EquipmentEntity getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentEntity equipment) {
        this.equipment = equipment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
