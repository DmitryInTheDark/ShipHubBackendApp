package ru.ship.ShipHub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ship.ShipHub.models.entity.LegalInfoEntity;

import java.util.Optional;

@Repository
public interface LegalInfoRepository extends JpaRepository<LegalInfoEntity, Long> {

    Optional<LegalInfoEntity> findByPerson_Id(Long personId);

}
