package ru.ship.ShipHub.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ship.ShipHub.models.dto.PersonDTO;
import ru.ship.ShipHub.models.dto.auth.RegistrationRequestDTO;
import ru.ship.ShipHub.models.entity.LegalInfoEntity;
import ru.ship.ShipHub.models.entity.PersonEntity;
import ru.ship.ShipHub.models.entity.PhysicalInfoEntity;
import ru.ship.ShipHub.repositories.LegalInfoRepository;
import ru.ship.ShipHub.repositories.PersonRepository;
import ru.ship.ShipHub.repositories.PhysicalRepository;
import ru.ship.ShipHub.util.MailUtil;
import ru.ship.ShipHub.util.Mapper;
import ru.ship.ShipHub.util.exceptions.PersonIsExistException;
import ru.ship.ShipHub.util.exceptions.PersonNotFoundException;

import java.util.Objects;
import java.util.Optional;

@Service
public class AuthService {

    private final Mapper mapper;
    private final Logger log;
    private final MailUtil mailUtil;
    private final PasswordEncoder passwordEncoder;
    private final LegalInfoRepository legalInfoRepository;
    private final PhysicalRepository physicalRepository;
    private final PersonRepository personRepository;

    public AuthService(Mapper mapper, MailUtil mailUtil, PasswordEncoder passwordEncoder, LegalInfoRepository legalInfoRepository, PhysicalRepository physicalRepository, PersonRepository personRepository) {
        this.mapper = mapper;
        this.mailUtil = mailUtil;
        this.passwordEncoder = passwordEncoder;
        this.legalInfoRepository = legalInfoRepository;
        this.physicalRepository = physicalRepository;
        this.log = LoggerFactory.getLogger(AuthService.class);
        this.personRepository = personRepository;
    }

    public PersonDTO login(
            String email,
            String password
    ){
        PersonEntity person = personRepository.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Неверный логин"));
        if (!person.getActive()){
            throw new BadCredentialsException("Активируйте аккаунт перед тем, как авторизоваться");
        }
        if (!passwordEncoder.matches(password, person.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }
        return mapper.map(person);
    }

    public void registration(
            RegistrationRequestDTO dto
    ){
        Optional<PersonEntity> personEntityOptional = personRepository.findByEmail(dto.email);
        personEntityOptional
                .filter(PersonEntity::getActive)
                .ifPresent(p -> { throw new PersonIsExistException("Пользователь с таким email уже существует"); });
        var code = generateCode();
        PersonEntity person;
        if(personEntityOptional.isPresent()){
            person = personEntityOptional.get();
            person.setPassword(passwordEncoder.encode(dto.password));
            person.setUsername(dto.name);
            person.setType(dto.type);
            person.setVerificationCode(code);
        }else{
            person = new PersonEntity(
                    dto.name, dto.email, passwordEncoder.encode(dto.password), false, code, dto.type
            );
        }
        personRepository.save(person);
        switch (person.getType()){
            case LEGAL -> {
                LegalInfoEntity legalInfo = mapper.map(dto.legalInfo);
                person.setLegalInfo(legalInfo);
                legalInfo.setPerson(person);
                legalInfoRepository.save(legalInfo);
            }
            case PHYSICAL -> {
                PhysicalInfoEntity physicalInfo = mapper.map(dto.physicalInfo);
                person.setPhysicalInfo(physicalInfo);
                physicalInfo.setPerson(person);
                physicalRepository.save(physicalInfo);
            }
        }
        try {
            sendMail(dto.email, code);
        }catch (Exception e){
            log.error("Mail send error", e);
            personRepository.findById(person.getId()).ifPresent( entity -> {
                entity.setActive(false);
                personRepository.save(entity);
                }
            );
        }
    }

    public PersonDTO verifyCode(String email, String code){
        var person = personRepository.findByEmail(email).orElseThrow(PersonNotFoundException::new);
        if (person.getActive()){
            throw new PersonIsExistException("Пользователь уже существует");
        }else if (!Objects.equals(code, person.getVerificationCode())){
            throw new IllegalArgumentException("Неправильный код");
        }else{
            person.setActive(true);
            person.setVerificationCode(null);
            personRepository.save(person);
        }
        return mapper.map(person);
    }

    public void sendMail(String mail, String message){
        mailUtil.sendMessage(mail, "Подтверждение регистрации", message);
    }

    public String generateCode() {
        StringBuilder code = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 5; i++) {
            code.append(chars.charAt((int) (Math.random() * (chars.length()))));
        }
        return code.toString();
    }
}
