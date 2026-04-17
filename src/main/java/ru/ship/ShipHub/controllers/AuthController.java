package ru.ship.ShipHub.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ship.ShipHub.models.dto.PersonDTO;
import ru.ship.ShipHub.models.request.LoginRequest;
import ru.ship.ShipHub.models.request.RegistrationRequest;
import ru.ship.ShipHub.models.request.VerifyCodeRequest;
import ru.ship.ShipHub.models.response.AuthResponse;
import ru.ship.ShipHub.services.AuthService;
import ru.ship.ShipHub.util.JWTUtil;

import java.util.Map;

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
    public ResponseEntity registration(
            @RequestBody @Valid RegistrationRequest registrationRequest
    ){
        service.registration(
                registrationRequest.email,
                registrationRequest.password,
                registrationRequest.name,
                registrationRequest.type);
        return ResponseEntity.ok().body(Map.of("response", "Код отправлен на почту " + registrationRequest.email));
//        return new AuthResponse(
//                jwtUtil.generateToken(person.getId(), person.getUsername()),
//                person
//        );
    }

    @PostMapping("/verify_code")
    public PersonDTO verifyCode(
            @RequestBody @Valid VerifyCodeRequest request
    ){
        return service.verifyCode(request.email, request.code);
    }
}
