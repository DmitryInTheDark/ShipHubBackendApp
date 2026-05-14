package ru.ship.ShipHub.services;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.ship.ShipHub.util.PersonType;
import ru.ship.ShipHub.util.exceptions.BadRequestException;
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

    public AuthService(
            Mapper mapper,
            MailUtil mailUtil,
            PasswordEncoder passwordEncoder,
            LegalInfoRepository legalInfoRepository,
            PhysicalRepository physicalRepository,
            PersonRepository personRepository
    ) {
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
        log.info(passwordEncoder.encode("password"));
        log.info(passwordEncoder.encode(person.getPassword()));
        if (!passwordEncoder.matches(password, person.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }
        return mapper.map(person);
    }

    @Transactional
    public void registration(
            RegistrationRequestDTO dto
    ){
        Optional<PersonEntity> personOptional = personRepository.findByEmail(dto.email);
        PersonEntity person;
        if (personOptional.isPresent()){
            person = personOptional.get();
            if (person.getActive()) throw new BadRequestException("Пользователь с таким email уже существует");
        }else {
            person = personRepository.save(mapper.map(dto));
            person.setPassword(passwordEncoder.encode(dto.password));
        }
        switch (dto.type){
            case MANAGER -> throw new BadRequestException("Нельзя создать пользователя с таким типом");
            case LEGAL -> {
                if (dto.legalInfo == null) throw new BadRequestException("Информация о юридическом лице пустая");
                LegalInfoEntity legalInfo = mapper.map(dto.legalInfo);
                if (personOptional.isPresent()){
                    if (person.getType() != dto.type){
                        clearPhysicalInfo(person);
                    }else{
                        legalInfo.setId(person.getLegalInfo().getId());
                    }
                }
                setLegalInfo(person, legalInfo);
            }
            case PHYSICAL -> {
                if (dto.physicalInfo == null) throw new BadRequestException("Информация о физическом лице пустая");
                PhysicalInfoEntity physicalInfo = mapper.map(dto.physicalInfo);
                if (personOptional.isPresent()){
                    if (person.getType() != dto.type){
                        clearLegalInfo(person);
                    }else{
                        physicalInfo.setId(person.getPhysicalInfo().getId());
                    }
                }
                setPhysicalInfo(person, physicalInfo);
            }
            default -> throw new BadRequestException("Неизвестный тип пользователя");
        }
        var code = generateCode();
        try {
            sendMail(dto.email, code);
            person.setVerificationCode(code);
        }catch (MailSendException e){
            throw new MailSendException("Не удалось отправить письмо, повторите попытку позже");
        }
        personRepository.save(person);
    }

    public PersonDTO verifyCode(String email, String code){
        var person = personRepository.findByEmail(email).orElseThrow(() -> new PersonNotFoundException("Пользователь не найден"));
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
        mailUtil.sendMessage(mail, "ЮК", message);
    }

    public String generateCode() {
        StringBuilder code = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 5; i++) {
            code.append(chars.charAt((int) (Math.random() * (chars.length()))));
        }
        return code.toString();
    }

    @Transactional
    public void clearPhysicalInfo(PersonEntity person){
        PhysicalInfoEntity physicalInfo = physicalRepository
                .findByPerson_Id(person.getId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена информация о физическом лице"));
        physicalRepository.delete(physicalInfo);
        person.setPhysicalInfo(null);
        person.setType(null);
    }

    @Transactional
    public void clearLegalInfo(PersonEntity person){
        LegalInfoEntity legalInfo = legalInfoRepository
                .findByPerson_Id(person.getId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена информация о юридическом лице"));
        legalInfoRepository.delete(legalInfo);
        person.setLegalInfo(null);
        person.setType(null);
    }

    @Transactional
    public void setPhysicalInfo(PersonEntity person, PhysicalInfoEntity physicalInfo){
        physicalInfo.setPerson(person);
        PhysicalInfoEntity savedInfo = physicalRepository.save(physicalInfo);
        person.setPhysicalInfo(savedInfo);
        person.setType(PersonType.PHYSICAL);
        personRepository.save(person);
    }

    @Transactional
    public void setLegalInfo(PersonEntity person, LegalInfoEntity legalInfo){
        legalInfo.setPerson(person);
        LegalInfoEntity savedInfo = legalInfoRepository.save(legalInfo);
        person.setLegalInfo(savedInfo);
        person.setType(PersonType.LEGAL);
        personRepository.save(person);
    }

}
