package ru.ship.ShipHub.models.dto.claim;

import jakarta.validation.constraints.*;
import ru.ship.ShipHub.util.TestType;

import java.time.LocalDateTime;

public class ClaimDTO {

    @Null
    private Long id;

    @Null
    private LocalDateTime dateCreate;

    @Null
    private LocalDateTime dateUpdate;

    @Size(min = 3, message = "Название организации не может быть меньше трёх символов")
    private String organizationName;

    @Size(min = 2, max = 255, message = "Имя клиента доолжно быть длинной от 2 до 255 символов")
    private String clientName;

    @NotEmpty(message = "Пустой контактный номер телефона")
    private String contactPhone;

    @NotEmpty
    @Size(min = 2, max = 255, message = "Почта должна быть длинной от 2 до 255 символов")
    private String email;

    private TestType testType;

    @NotNull
    private EquipmentDTO equipment;

    private boolean isCustomType;

    private String customTestName;

    private String additionalInfo;

    public ClaimDTO() {}

    public ClaimDTO(
            LocalDateTime dateCreate,
            Long id,
            String organizationName,
            LocalDateTime dateUpdate,
            String contactPhone,
            String clientName,
            String email,
            TestType testType,
            EquipmentDTO equipment,
            boolean isCustomType,
            String customTestName,
            String additionalInfo
    ) {
        this.dateCreate = dateCreate;
        this.id = id;
        this.organizationName = organizationName;
        this.dateUpdate = dateUpdate;
        this.contactPhone = contactPhone;
        this.clientName = clientName;
        this.email = email;
        this.testType = testType;
        this.equipment = equipment;
        this.isCustomType = isCustomType;
        this.customTestName = customTestName;
        this.additionalInfo = additionalInfo;
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

    public EquipmentDTO getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentDTO equipment) {
        this.equipment = equipment;
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
}
