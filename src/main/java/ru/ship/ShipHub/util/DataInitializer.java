package ru.ship.ShipHub.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ship.ShipHub.models.entity.PersonEntity;
import ru.ship.ShipHub.repositories.PersonRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initManagers(PersonRepository personRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            if (personRepository.findByEmail("email").isEmpty()) {
                PersonEntity manager = new PersonEntity();
                manager.setEmail("email");
                manager.setUsername("Анатолий");
                manager.setPassword(passwordEncoder.encode("password"));
                manager.setType(PersonType.MANAGER);
                manager.setActive(true);
                personRepository.save(manager);
            }
        };
    }
}
