package ru.ship.ShipHub.util;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ship.ShipHub.models.dto.LegalInfoDTO;
import ru.ship.ShipHub.models.dto.PersonDTO;
import ru.ship.ShipHub.models.dto.PhysicalInfoDTO;
import ru.ship.ShipHub.models.entity.LegalInfoEntity;
import ru.ship.ShipHub.models.entity.PersonEntity;
import ru.ship.ShipHub.models.entity.PhysicalInfoEntity;

@Component
public class Mapper {

    @Autowired
    private ModelMapper mapper;

    public PersonEntity map(PersonDTO personDTO){
        return mapper.map(personDTO, PersonEntity.class);
    }

    public PersonDTO map(PersonEntity personEntity) {
        return mapper.map(personEntity, PersonDTO.class);
    }

    public LegalInfoEntity map(LegalInfoDTO dto){
        if (dto == null) throw new NullPointerException("пустая информация о юридическом лице");
        return mapper.map(dto, LegalInfoEntity.class);
    }

    public PhysicalInfoEntity map(PhysicalInfoDTO dto){
        if (dto == null) throw new NullPointerException("пустая информация о физическом лице");
        return mapper.map(dto, PhysicalInfoEntity.class);
    }

}
