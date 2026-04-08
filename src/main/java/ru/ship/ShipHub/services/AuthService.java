package ru.ship.ShipHub.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ship.ShipHub.models.entity.PersonEntity;
import ru.ship.ShipHub.repositories.PersonRepository;
import ru.ship.ShipHub.security.PersonDetails;
import ru.ship.ShipHub.util.Mapper;
import ru.ship.ShipHub.util.exceptions.PersonIsExistException;

import javax.management.openmbean.InvalidKeyException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthService {

    private final Mapper mapper;
    private final Logger log;

    private final PersonRepository personRepository;

    public AuthService(Mapper mapper, PersonRepository personRepository) {
        this.mapper = mapper;
        this.log = LoggerFactory.getLogger(AuthService.class);
        this.personRepository = personRepository;
    }

    public PersonDetails login(
            String email,
            String password
    ){
        PersonEntity person = personRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
        if (!Objects.equals(person.getPassword(), password)) throw new InvalidKeyException();
        return new PersonDetails(person);
    }

    public PersonDetails registration(
            String email,
            String password,
            String username
    ){
        log.info("service reg");
        Optional<PersonEntity> personEntityOptional = personRepository.findByEmail(email);
        if (personEntityOptional.isPresent()){
            throw new PersonIsExistException();
        }
        PersonEntity entity = new PersonEntity(
            username, email, password
        );
        return new PersonDetails(personRepository.save(entity));
    }
}
