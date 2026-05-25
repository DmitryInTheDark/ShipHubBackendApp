package ru.ship.ShipHub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ship.ShipHub.models.entity.ClaimEntity;
import ru.ship.ShipHub.models.entity.MessageEntity;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    long countByClaim(ClaimEntity claim);

    List<MessageEntity> findAllByClaimOrderByDateCreatedAsc(ClaimEntity claim);
}
