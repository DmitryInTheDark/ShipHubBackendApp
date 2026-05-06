package ru.ship.ShipHub.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ship.ShipHub.models.entity.ClaimEntity;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<ClaimEntity, Long> {

    @Override
    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    List<ClaimEntity> findAll();

    @Override
    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    Page<ClaimEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    List<ClaimEntity> findByWhoCreateId(Long userId);

    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    Page<ClaimEntity> findByWhoCreateId(Long userId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    Optional<ClaimEntity> findById(Long aLong);
}
