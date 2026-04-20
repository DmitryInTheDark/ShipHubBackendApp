package ru.ship.ShipHub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ship.ShipHub.models.entity.EquipmentEntity;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long> {
}
