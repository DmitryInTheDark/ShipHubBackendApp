package ru.ship.ShipHub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ship.ShipHub.models.entity.ClaimEntity;
import ru.ship.ShipHub.models.entity.DocumentEntity;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    long countByClaim(ClaimEntity claim);

    List<DocumentEntity> findAllByClaim(ClaimEntity claim);

}
