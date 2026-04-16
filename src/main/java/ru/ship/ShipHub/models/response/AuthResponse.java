package ru.ship.ShipHub.models.response;

import ru.ship.ShipHub.models.dto.PersonDTO;

public class AuthResponse {

    public String token;
    public PersonDTO person;

    public AuthResponse(String token, PersonDTO person){
        this.token = token;
        this.person = person;
    }
}
