package ru.ship.ShipHub.services.details;

import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ship.ShipHub.models.entity.PersonEntity;
import ru.ship.ShipHub.repositories.PersonRepository;
import ru.ship.ShipHub.security.PersonDetails;

import java.util.NoSuchElementException;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public PersonDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        PersonEntity user = personRepository.findByUsername(username).orElseThrow(NoSuchElementException::new);
        return new PersonDetails(user);
    }

    public PersonDetails loadById(Long id) throws EntityNotFoundException {
        return new PersonDetails(personRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }
}
