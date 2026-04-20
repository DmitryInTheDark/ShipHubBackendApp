package ru.ship.ShipHub.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.ship.ShipHub.models.dto.claim.ClaimDTO;
import ru.ship.ShipHub.models.entity.ClaimEntity;
import ru.ship.ShipHub.models.entity.EquipmentEntity;
import ru.ship.ShipHub.models.entity.EquipmentImageEntity;
import ru.ship.ShipHub.repositories.*;
import ru.ship.ShipHub.util.EquipmentType;
import ru.ship.ShipHub.util.MailUtil;
import ru.ship.ShipHub.util.Mapper;
import ru.ship.ShipHub.util.TestType;
import ru.ship.ShipHub.util.exceptions.BadRequestException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClaimsService {

    private final Mapper mapper;
    private final Logger log;
    private final PersonRepository personRepository;
    private final ClaimRepository claimRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentImageRepository equipmentImageRepository;

    public ClaimsService(Mapper mapper, MailUtil mailUtil, PasswordEncoder passwordEncoder, LegalInfoRepository legalInfoRepository, PhysicalRepository physicalRepository, PersonRepository personRepository, ClaimRepository claimRepository, EquipmentRepository equipmentRepository, EquipmentImageRepository equipmentImageRepository) {
        this.mapper = mapper;
        this.claimRepository = claimRepository;
        this.equipmentRepository = equipmentRepository;
        this.equipmentImageRepository = equipmentImageRepository;
        this.log = LoggerFactory.getLogger(AuthService.class);
        this.personRepository = personRepository;
    }

    public ClaimDTO createClaim(
            List<MultipartFile> photos,
            ClaimDTO dto
    ){
        ClaimEntity claim = mapper.map(dto);
        claim.setDateCreate(LocalDateTime.now());
        EquipmentEntity equipment = mapper.map(dto.getEquipment());
        equipmentRepository.save(equipment);
        claim.setEquipment(equipment);
        if (claim.isCustomType() && claim.getTestType() != TestType.OTHER) {
            throw new BadRequestException("Заявка с нестандартным типом тестирования не может содержать другой тип тестирования");
        }
        if (claim.getEquipment().isCustomType() && claim.getEquipment().getEquipmentType() != EquipmentType.OTHER) {
            throw new BadRequestException("Оборудование с нестандартным типом не может содержать другой тип оборудования");
        }
        photos.stream()
                .filter(file -> !file.isEmpty())
                .forEach(file -> {
                    if (!file.isEmpty()){
                        try {
                            EquipmentImageEntity photoEntity = new EquipmentImageEntity(
                                    file.getBytes(), claim.getEquipment(), "description"
                            );

                            equipmentImageRepository.save(photoEntity);
                        } catch (IOException e) {
                            log.error("Ошибка чтения файла: ", e);
                        }
                    }
                });
        ClaimEntity savedClaim = claimRepository.save(claim);
        return mapper.map(savedClaim);
    }

    public List<ClaimDTO> getAllClaims() {
        return claimRepository.findAll().stream().map(mapper::map).toList();
    }
}
