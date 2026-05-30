package ru.ship.ShipHub.models.dto;

import java.time.LocalDateTime;

public class NotificationDTO {

    private Long claimId;
    private LocalDateTime lastUpdateAt;
    private String lastUpdateText;
    private Long lastUpdateById;
    private String lastUpdateByName;

    public NotificationDTO() {}

    public NotificationDTO(Long claimId, LocalDateTime lastUpdateAt, String lastUpdateText, Long lastUpdateById, String lastUpdateByName) {
        this.claimId = claimId;
        this.lastUpdateAt = lastUpdateAt;
        this.lastUpdateText = lastUpdateText;
        this.lastUpdateById = lastUpdateById;
        this.lastUpdateByName = lastUpdateByName;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public LocalDateTime getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(LocalDateTime lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public String getLastUpdateText() {
        return lastUpdateText;
    }

    public void setLastUpdateText(String lastUpdateText) {
        this.lastUpdateText = lastUpdateText;
    }

    public Long getLastUpdateById() {
        return lastUpdateById;
    }

    public void setLastUpdateById(Long lastUpdateById) {
        this.lastUpdateById = lastUpdateById;
    }

    public String getLastUpdateByName() {
        return lastUpdateByName;
    }

    public void setLastUpdateByName(String lastUpdateByName) {
        this.lastUpdateByName = lastUpdateByName;
    }
}
