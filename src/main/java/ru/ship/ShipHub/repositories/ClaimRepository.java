package ru.ship.ShipHub.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ship.ShipHub.models.entity.ClaimEntity;

import java.util.List;

public interface ClaimRepository extends JpaRepository<ClaimEntity, Long> {

    @Override
    @EntityGraph(attributePaths = { "equipment" })
    List<ClaimEntity> findAll();

    @EntityGraph(attributePaths = { "equipment" })
    List<ClaimEntity> findByWhoCreateId(Long userId);
}
