package ru.ship.ShipHub.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ship.ShipHub.models.entity.ClaimEntity;
import ru.ship.ShipHub.models.entity.PersonEntity;
import ru.ship.ShipHub.util.ClaimStatus;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<ClaimEntity, Long> {

    long countByStatus(ClaimStatus status);

    long countByWhoCreate(PersonEntity person);

    @Query("SELECT COUNT(e) FROM ClaimEntity e WHERE e.status != :status")
    long countWithoutStatus(ClaimStatus status);

    @Query("SELECT COUNT(e) FROM ClaimEntity e WHERE e.status = :status")
    long countWithStatus(ClaimStatus status);

    @Query("SELECT COUNT(e) FROM ClaimEntity e WHERE e.status != :status AND e.whoCreate.id = :personId")
    long countWithoutStatusByWhoCreateId(ClaimStatus status, long personId);

    @Query("SELECT COUNT(e) FROM ClaimEntity e WHERE e.status = :status AND e.whoCreate.id = :personId")
    long countWithStatusByWhoCreateId(ClaimStatus status, long personId);

    @Override
    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    List<ClaimEntity> findAll();

    @Query("SELECT e FROM ClaimEntity e WHERE e.status = :status")
    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    List<ClaimEntity> findByStatus(ClaimStatus status, Pageable pageable);

    @Query("SELECT e FROM ClaimEntity e WHERE e.status = :status AND e.whoCreate.id = :personId")
    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    List<ClaimEntity> findByStatusByWhoCreateId(ClaimStatus status, long personId, Pageable pageable);

    @Query("SELECT e FROM ClaimEntity e WHERE e.status != :status")
    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    List<ClaimEntity> findWithoutStatus(ClaimStatus status, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    Page<ClaimEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    List<ClaimEntity> findByWhoCreateId(Long userId);

    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    Page<ClaimEntity> findByWhoCreateId(Long userId, Pageable pageable);

    @Query("SELECT e FROM ClaimEntity e WHERE e.whoCreate.id = :userId AND e.status != :status")
    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    Page<ClaimEntity> findWithoutStatusByWhoCreateId(Long userId, ClaimStatus status, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = { "equipment", "equipment.images", "documentsIds"})
    Optional<ClaimEntity> findById(Long aLong);
}
