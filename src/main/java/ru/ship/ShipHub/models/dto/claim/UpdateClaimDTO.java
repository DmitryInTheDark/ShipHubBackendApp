package ru.ship.ShipHub.models.dto.claim;

import ru.ship.ShipHub.util.ClaimStatus;

public class UpdateClaimDTO {

    private ClaimStatus status;

    private String updateInfo;

    public UpdateClaimDTO(){}

    public UpdateClaimDTO(ClaimStatus status, String updateInfo){
        this.status = status;
        this.updateInfo = updateInfo;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }
}
