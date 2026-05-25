package ru.ship.ShipHub.models.dto.claim;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class MessageCreateDTO {

    @NotNull
    public Long claimId;

    @NotEmpty
    public String text;

    public MessageCreateDTO() {}

    public MessageCreateDTO(Long claimId, String text) {
        this.claimId = claimId;
        this.text = text;
    }
}
