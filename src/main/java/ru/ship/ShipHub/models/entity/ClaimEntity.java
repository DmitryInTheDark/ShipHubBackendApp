package ru.ship.ShipHub.models.entity;

import jakarta.persistence.*;
import ru.ship.ShipHub.util.ClaimStatus;
import ru.ship.ShipHub.util.TestType;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class ClaimEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_create")
    private LocalDateTime dateCreate;

    @Column(name = "date_update")
    private LocalDateTime dateUpdate;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "email")
    private String email;

    @Column(name = "test_type")
    @Enumerated(value = EnumType.STRING)
    private TestType testType;

    @Column(name = "is_custom_type")
    private boolean isCustomType;

    @Column(name = "custom_test_name")
    private String customTestName;

    @Column(name = "additional_info")
    private String additionalInfo;

    @OneToOne
    @JoinColumn(name = "equipment_id", referencedColumnName = "id")
    private EquipmentEntity equipment;

    @ManyToOne(targetEntity = PersonEntity.class)
    private PersonEntity whoCreate;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private ClaimStatus status;

    @Column(name = "last_update")
    private String lastUpdate;

    public ClaimEntity() {}

    public ClaimEntity(
            LocalDateTime dateCreate,
            LocalDateTime dateUpdate,
            String organizationName,
            String clientName,
            String contactPhone,
            String email,
            TestType testType,
            boolean isCustomType,
            String customTestName,
            String additionalInfo,
            PersonEntity whoCreate,
            ClaimStatus status,
            String lastUpdate
    ) {
        this.dateCreate = dateCreate;
        this.dateUpdate = dateUpdate;
        this.organizationName = organizationName;
        this.clientName = clientName;
        this.contactPhone = contactPhone;
        this.email = email;
        this.testType = testType;
        this.isCustomType = isCustomType;
        this.customTestName = customTestName;
        this.additionalInfo = additionalInfo;
        this.whoCreate = whoCreate;
        this.status = status;
        this.lastUpdate = lastUpdate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDateTime dateCreate) {
        this.dateCreate = dateCreate;
    }

    public LocalDateTime getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(LocalDateTime dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TestType getTestType() {
        return testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    public boolean isCustomType() {
        return isCustomType;
    }

    public void setCustomType(boolean customType) {
        isCustomType = customType;
    }

    public String getCustomTestName() {
        return customTestName;
    }

    public void setCustomTestName(String customTestName) {
        this.customTestName = customTestName;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public EquipmentEntity getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentEntity equipment) {
        this.equipment = equipment;
    }

    public PersonEntity getWhoCreate() {
        return whoCreate;
    }

    public void setWhoCreate(PersonEntity whoCreate) {
        this.whoCreate = whoCreate;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
