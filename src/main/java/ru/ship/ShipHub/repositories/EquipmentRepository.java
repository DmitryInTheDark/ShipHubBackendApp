package ru.ship.ShipHub.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ship.ShipHub.models.entity.EquipmentEntity;

import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long> {

    @Override
    @EntityGraph(attributePaths = { "images" })
    Optional<EquipmentEntity> findById(Long aLong);

}
