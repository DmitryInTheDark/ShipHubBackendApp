package ru.ship.ShipHub.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ship.ShipHub.models.request.LoginRequest;
import ru.ship.ShipHub.models.request.RegistrationRequest;
import ru.ship.ShipHub.models.response.AuthResponse;
import ru.ship.ShipHub.services.AuthService;
import ru.ship.ShipHub.util.JWTUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final Logger log;
    private final JWTUtil jwtUtil;

    public AuthController(AuthService service, JWTUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
        this.log = LoggerFactory.getLogger(AuthController.class);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody @Valid LoginRequest loginRequest
    ){
        var person = service.login(loginRequest.email, loginRequest.password);
        return new AuthResponse(
                jwtUtil.generateToken(person.getId(), person.getUsername()),
                person
        );
    }

    @PostMapping("/registration")
    public AuthResponse registration(
            @RequestBody @Valid RegistrationRequest registrationRequest
    ){
        var person = service.registration(
                registrationRequest.email,
                registrationRequest.password,
                registrationRequest.name);
        log.info("Controller reg");
        return new AuthResponse(
                jwtUtil.generateToken(person.getId(), person.getUsername()),
                person
        );
    }

}
