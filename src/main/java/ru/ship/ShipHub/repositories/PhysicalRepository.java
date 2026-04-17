package ru.ship.ShipHub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ship.ShipHub.models.entity.PhysicalInfoEntity;

@Repository
public interface PhysicalRepository extends JpaRepository<PhysicalInfoEntity, Long> {
}
