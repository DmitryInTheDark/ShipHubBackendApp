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
import ru.ship.ShipHub.models.dto.auth.LoginRequestDTO;
import ru.ship.ShipHub.models.dto.auth.RegistrationRequestDTO;
import ru.ship.ShipHub.models.dto.auth.VerifyCodeRequestDTO;
import ru.ship.ShipHub.models.response.AuthResponse;
import ru.ship.ShipHub.services.AuthService;
import ru.ship.ShipHub.util.JWTUtil;
import ru.ship.ShipHub.util.Mapper;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final Logger log;
    private final JWTUtil jwtUtil;
    private final Mapper mapper;

    public AuthController(AuthService service, JWTUtil jwtUtil, Mapper mapper) {
        this.service = service;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
        this.log = LoggerFactory.getLogger(AuthController.class);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody @Valid LoginRequestDTO loginRequest
    ){
        var user = service.login(loginRequest.email, loginRequest.password);
        return new AuthResponse(
                jwtUtil.generateToken(user.getId(), user.getUsername(), user.getType().toString()),
                user
        );
    }

    @PostMapping("/registration")
    public ResponseEntity registration(
            @RequestBody @Valid RegistrationRequestDTO registrationRequest
    ){
        service.registration(registrationRequest);
        return ResponseEntity.ok().body(Map.of("response", "Код отправлен на почту " + registrationRequest.email));
    }

    @PostMapping("/verify_code")
    public PersonDTO verifyCode(
            @RequestBody @Valid VerifyCodeRequestDTO request
    ){
        return service.verifyCode(request.email, request.code);
    }
}
