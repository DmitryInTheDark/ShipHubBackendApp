package ru.ship.ShipHub.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ship.ShipHub.models.request.LoginRequest;
import ru.ship.ShipHub.models.dto.PersonDTO;
import ru.ship.ShipHub.models.request.RegistrationRequest;
import ru.ship.ShipHub.services.AuthService;
import ru.ship.ShipHub.util.Mapper;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final Mapper mapper;
    private final Logger log;

    public AuthController(AuthService service, Mapper mapper) {
        this.service = service;
        this.mapper = mapper;
        this.log = LoggerFactory.getLogger(AuthController.class);
    }

    @PostMapping("/login")
    public PersonDTO login(
            @RequestBody @Valid LoginRequest loginRequest
    ){
        return service.login(loginRequest.email, loginRequest.password).getBaseInfo();
    }

    @PostMapping("/registration")
    public PersonDTO registration(
            @RequestBody @Valid RegistrationRequest registrationRequest
    ){
        log.info("Controller reg");
        return service.registration(
                registrationRequest.email,
                registrationRequest.password,
                registrationRequest.name
        ).getBaseInfo();
    }

}
