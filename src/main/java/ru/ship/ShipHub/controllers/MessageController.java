package ru.ship.ShipHub.controllers;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ship.ShipHub.config.security.PersonDetails;
import ru.ship.ShipHub.models.dto.ListDTO;
import ru.ship.ShipHub.models.dto.claim.MessageCreateDTO;
import ru.ship.ShipHub.models.dto.claim.MessageDTO;
import ru.ship.ShipHub.services.MessageService;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public MessageDTO createMessage(
            @RequestBody @Valid MessageCreateDTO dto,
            @AuthenticationPrincipal PersonDetails personDetails
    ) {
        return messageService.createMessage(dto, personDetails);
    }

    @GetMapping
    public ListDTO<MessageDTO> getMessagesByClaim(
            @RequestParam("claim_id") Long claimId,
            @AuthenticationPrincipal PersonDetails personDetails
    ) {
        return messageService.getMessagesByClaim(claimId, personDetails);
    }
}
