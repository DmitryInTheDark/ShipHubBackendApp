package ru.ship.ShipHub.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ship.ShipHub.models.dto.auth.*;
import ru.ship.ShipHub.models.response.AuthResponse;
import ru.ship.ShipHub.services.AuthService;
import ru.ship.ShipHub.util.JWTUtil;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final JWTUtil jwtUtil;

    public AuthController(AuthService service, JWTUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
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
    public AuthResponse verifyCode(
            @RequestBody @Valid VerifyCodeRequestDTO request
    ){
        var person = service.verifyCode(request.email, request.code);
        return new AuthResponse(
                jwtUtil.generateToken(person.getId(), person.getUsername(), person.getType().toString()),
                person
        );
    }

    @PostMapping("/restore_password/request")
    public ResponseEntity requestToRestorePassword(
            @RequestBody @Valid RestorePasswordEmailRequestDTO dto
    ){
        service.requestToRestorePassword(dto.email());
        return ResponseEntity.ok().body(Map.of("response", "Код отправлен на почту " + dto.email()));
    }

    @PostMapping("/restore_password")
    public ResponseEntity verifyRestorePasswordCode(
            @RequestBody @Valid VerifyRestorePasswordCodeDTO dto
    ){
        var token = service.validateRestorePasswordCode(dto.email(), dto.code());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PatchMapping("/restore_password")
    public AuthResponse restorePassword(
            @RequestBody @Valid RestorePasswordDTO dto
    ){
        var person = service.restorePassword(dto);
        return new AuthResponse(
                jwtUtil.generateToken(person.getId(), person.getUsername(), person.getType().toString()),
                person
        );
    }

}
