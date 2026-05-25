package ru.ship.ShipHub.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
