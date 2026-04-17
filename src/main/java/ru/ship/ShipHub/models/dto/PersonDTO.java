package ru.ship.ShipHub.models.dto;

import ru.ship.ShipHub.util.PersonType;

public class PersonDTO {

    private Integer id;

    private String username;

    private String email;

    private PersonType type;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PersonDTO(Integer id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public PersonDTO(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PersonType getType() {
        return type;
    }

    public void setType(PersonType type) {
        this.type = type;
    }
}
