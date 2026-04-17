package ru.ship.ShipHub.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ship.ShipHub.models.entity.PersonEntity;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Integer> {

    @EntityGraph(attributePaths = {"legalInfo", "physicalInfo"})
    Optional<PersonEntity> findByUsername(String username);

    @EntityGraph(attributePaths = {"legalInfo", "physicalInfo"})
    Optional<PersonEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
