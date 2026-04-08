package ru.ship.ShipHub.util;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.ship.ShipHub.models.dto.PersonDTO;
import ru.ship.ShipHub.models.entity.PersonEntity;
import ru.ship.ShipHub.models.request.RegistrationRequest;
import tools.jackson.databind.ObjectMapper;

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

}
