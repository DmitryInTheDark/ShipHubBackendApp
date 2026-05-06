package ru.ship.ShipHub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ship.ShipHub.models.entity.DocumentEntity;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
}
