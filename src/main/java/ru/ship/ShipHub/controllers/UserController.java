package ru.ship.ShipHub.controllers;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ship.ShipHub.config.security.PersonDetails;
import ru.ship.ShipHub.models.dto.PersonDTO;
import ru.ship.ShipHub.models.dto.UserUpdateDTO;
import ru.ship.ShipHub.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}")
    public PersonDTO updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO updateRequest
    ) {
        return userService.updateUser(id, updateRequest);
    }

    @GetMapping
    public PersonDTO updateUser(
            @AuthenticationPrincipal PersonDetails personDetails
    ) {
        return userService.getUser(personDetails);
    }
}
