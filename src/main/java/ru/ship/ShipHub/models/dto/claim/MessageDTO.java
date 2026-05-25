package ru.ship.ShipHub.models.dto.claim;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class   MessageDTO {

    public Long id;

    @NotNull
    public Long claimId;

    public Long senderId;

    @NotEmpty
    public String text;

    public LocalDateTime dateCreated;

    public MessageDTO() {}

    public MessageDTO(Long id, Long claimId, Long senderId, String text, LocalDateTime dateCreated) {
        this.id = id;
        this.claimId = claimId;
        this.senderId = senderId;
        this.text = text;
        this.dateCreated = dateCreated;
    }
}
