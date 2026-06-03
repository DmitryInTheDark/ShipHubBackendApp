package ru.ship.ShipHub.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ship.ShipHub.config.security.PersonDetails;
import ru.ship.ShipHub.models.dto.PersonDTO;
import ru.ship.ShipHub.models.dto.UserUpdateDTO;
import ru.ship.ShipHub.models.entity.LegalInfoEntity;
import ru.ship.ShipHub.models.entity.PersonEntity;
import ru.ship.ShipHub.models.entity.PhysicalInfoEntity;
import ru.ship.ShipHub.repositories.PersonRepository;
import ru.ship.ShipHub.util.Mapper;
import ru.ship.ShipHub.util.PersonType;
import ru.ship.ShipHub.util.exceptions.BadRequestException;
import ru.ship.ShipHub.util.exceptions.PersonNotFoundException;

@Service
public class UserService {

    private final PersonRepository personRepository;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(PersonRepository personRepository, Mapper mapper, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public PersonDTO updateUser(Long id, UserUpdateDTO dto) {
        PersonEntity person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);

        if (!person.getEmail().equals(dto.getEmail()) && personRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Пользователь с такой почтой уже существует");
        }

        person.setUsername(dto.getUsername());
        person.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            person.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getIsActive() != null) {
            person.setIsActive(dto.getIsActive());
        }

        if (dto.getVerificationCode() != null) {
            person.setVerificationCode(dto.getVerificationCode());
        }

        PersonType currentType = person.getType();
        switch (currentType) {
            case LEGAL -> updateLegalInfo(person, dto);
            case PHYSICAL -> updatePhysicalInfo(person, dto);
            default -> validateInfoForManager(dto);
        }

        PersonEntity saved = personRepository.save(person);
        return mapper.map(saved);
    }

    private void updateLegalInfo(PersonEntity person, UserUpdateDTO dto) {
        if (dto.getPhysicalInfo() != null) {
            throw new BadRequestException("Нельзя обновлять физическую информацию для юридического пользователя");
        }
        if (dto.getLegalInfo() == null) {
            return;
        }

        LegalInfoEntity currentLegalInfo = person.getLegalInfo();
        if (currentLegalInfo == null) {
            currentLegalInfo = mapper.map(dto.getLegalInfo());
            currentLegalInfo.setPerson(person);
            person.setLegalInfo(currentLegalInfo);
        } else {
            mapper.update(currentLegalInfo, mapper.map(dto.getLegalInfo()));
        }
    }

    private void updatePhysicalInfo(PersonEntity person, UserUpdateDTO dto) {
        if (dto.getLegalInfo() != null) {
            throw new BadRequestException("Нельзя обновлять юридическую информацию для физического пользователя");
        }
        if (dto.getPhysicalInfo() == null) {
            return;
        }

        PhysicalInfoEntity currentPhysicalInfo = person.getPhysicalInfo();
        if (currentPhysicalInfo == null) {
            currentPhysicalInfo = mapper.map(dto.getPhysicalInfo());
            currentPhysicalInfo.setPerson(person);
            person.setPhysicalInfo(currentPhysicalInfo);
        } else {
            mapper.update(currentPhysicalInfo, mapper.map(dto.getPhysicalInfo()));
        }
    }

    private void validateInfoForManager(UserUpdateDTO dto) {
        if (dto.getLegalInfo() != null || dto.getPhysicalInfo() != null) {
            throw new BadRequestException("Менеджеру нельзя обновлять данные юридического или физического лица");
        }
    }

    public PersonDTO getUser(PersonDetails personDetails) {
        return mapper.map(personRepository.findById(personDetails.getPerson().getId()).orElseThrow(PersonNotFoundException::new));
    }
}
