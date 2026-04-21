package ru.ship.ShipHub.util;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ship.ShipHub.models.dto.LegalInfoDTO;
import ru.ship.ShipHub.models.dto.PersonDTO;
import ru.ship.ShipHub.models.dto.PhysicalInfoDTO;
import ru.ship.ShipHub.models.dto.claim.ClaimDTO;
import ru.ship.ShipHub.models.dto.claim.EquipmentDTO;
import ru.ship.ShipHub.models.entity.*;

@Component
public class Mapper {

    private static final Logger log = LoggerFactory.getLogger(Mapper.class);
    @Autowired
    private ModelMapper mapper;

    public PersonEntity map(PersonDTO personDTO){
        return mapper.map(personDTO, PersonEntity.class);
    }

    public PersonDTO map(PersonEntity personEntity) {
        return mapper.map(personEntity, PersonDTO.class);
    }

    public LegalInfoEntity map(LegalInfoDTO dto){
        return mapper.map(dto, LegalInfoEntity.class);
    }

    public PhysicalInfoEntity map(PhysicalInfoDTO dto){
        return mapper.map(dto, PhysicalInfoEntity.class);
    }

    public ClaimEntity map(ClaimDTO dto){
        ClaimEntity c = new ClaimEntity();

        c.setDateCreate(dto.getDateCreate());
        c.setOrganizationName(dto.getOrganizationName());
        c.setClientName(dto.getClientName());
        c.setContactPhone(dto.getContactPhone());
        c.setEmail(dto.getEmail());
        c.setTestType(dto.getTestType());
        c.setCustomType(dto.isCustomType());
        c.setCustomTestName(dto.getCustomTestName());
        c.setAdditionalInfo(dto.getAdditionalInfo());

        EquipmentEntity e = map(dto.getEquipment());
        c.setEquipment(e);

        return c;
    }

    public ClaimDTO map(ClaimEntity entity){
        return mapper.map(entity, ClaimDTO.class);
    }

    public EquipmentDTO map(EquipmentEntity entity){
        return new EquipmentDTO(
                entity.getId(),
                entity.getEquipmentType(),
                entity.getName(),
                entity.getManufacturer(),
                entity.getSerialNumber(),
                entity.getCount(),
                entity.getCustomType(),
                entity.isCustomType()
        );
    }

    public EquipmentEntity map(EquipmentDTO dto){
        EquipmentEntity e = new EquipmentEntity();

        e.setEquipmentType(dto.getEquipmentType());
        e.setName(dto.getName());
        e.setManufacturer(dto.getManufacturer());
        e.setSerialNumber(dto.getSerialNumber());
        e.setCount(dto.getCount());
        e.setCustomType(dto.isCustomType());
        e.setCustomType(dto.getCustomType());

        return e;
    }

//    public EquipmentImageEntity map(ClaimEntity entity){
//        return mapper.map(entity, ClaimDTO.class);
//    }
//
//    public ClaimDTO map(ClaimEntity entity){
//        return mapper.map(entity, ClaimDTO.class);
//    }

}
