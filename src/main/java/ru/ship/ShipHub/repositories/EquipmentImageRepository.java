package ru.ship.ShipHub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ship.ShipHub.models.entity.EquipmentImageEntity;

public interface EquipmentImageRepository extends JpaRepository<EquipmentImageEntity, Long> {
}
