package ru.ship.ShipHub.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ship.ShipHub.config.security.PersonDetails;
import ru.ship.ShipHub.models.dto.ListDTO;
import ru.ship.ShipHub.models.dto.claim.MessageCreateDTO;
import ru.ship.ShipHub.models.dto.claim.MessageDTO;
import ru.ship.ShipHub.models.entity.ClaimEntity;
import ru.ship.ShipHub.models.entity.MessageEntity;
import ru.ship.ShipHub.repositories.ClaimRepository;
import ru.ship.ShipHub.repositories.MessageRepository;
import ru.ship.ShipHub.util.Mapper;
import ru.ship.ShipHub.util.exceptions.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ClaimRepository claimRepository;
    private final Mapper mapper;

    public MessageService(MessageRepository messageRepository,
                          ClaimRepository claimRepository,
                          Mapper mapper) {
        this.messageRepository = messageRepository;
        this.claimRepository = claimRepository;
        this.mapper = mapper;
    }

    @Transactional
    public MessageDTO createMessage(MessageCreateDTO dto, PersonDetails personDetails) {
        ClaimEntity claim = claimRepository.findById(dto.claimId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));
        if (isNotManager(personDetails) && !claim.getWhoCreate().getId().equals(personDetails.getPerson().getId())) {
            throw new BadRequestException("Доступ запрещён к этой заявке");
        }
        MessageEntity message = new MessageEntity(
                dto.text,
                LocalDateTime.now(),
                claim,
                personDetails.getPerson()
        );
        MessageEntity saved = messageRepository.save(message);
        return mapper.map(saved);
    }

    public ListDTO<MessageDTO> getMessagesByClaim(Long claimId, PersonDetails personDetails) {
        ClaimEntity claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));
        if (isNotManager(personDetails) && !claim.getWhoCreate().getId().equals(personDetails.getPerson().getId())) {
            throw new BadRequestException("Доступ запрещён к этой заявке");
        }
        List<MessageDTO> messages = messageRepository.findAllByClaimOrderByDateCreatedAsc(claim)
                .stream()
                .map(mapper::map)
                .toList();
        return new ListDTO<>(messages.size(), messages);
    }

    private boolean isNotManager(PersonDetails personDetails) {
        return personDetails.getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"));
    }
}
