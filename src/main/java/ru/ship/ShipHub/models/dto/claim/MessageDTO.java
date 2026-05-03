package ru.ship.ShipHub.models.dto.claim;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;

import java.time.LocalDateTime;

public class MessageDTO {

    @NotEmpty
    public String text;

    @Null
    public LocalDateTime dateCreated;

    public MessageDTO() {}

    public MessageDTO(String text, LocalDateTime dateCreated) {
        this.text = text;
        this.dateCreated = dateCreated;
    }

}
