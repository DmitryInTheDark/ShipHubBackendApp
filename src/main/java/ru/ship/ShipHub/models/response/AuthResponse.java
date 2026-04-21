package ru.ship.ShipHub.models.response;

import ru.ship.ShipHub.models.dto.PersonDTO;

public class AuthResponse {

    public String token;
    public PersonDTO user;

    public AuthResponse(String token, PersonDTO user){
        this.token = token;
        this.user = user;
    }
}
