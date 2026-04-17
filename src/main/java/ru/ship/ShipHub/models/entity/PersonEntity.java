package ru.ship.ShipHub.models.entity;

import jakarta.persistence.*;
import ru.ship.ShipHub.util.PersonType;

@Entity
@Table(name = "users")
public class PersonEntity {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private PersonType type;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PersonEntity(){}

    public PersonEntity(String username, String email, String password, Boolean isActive, String verificationCode, PersonType type) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.verificationCode = verificationCode;
        this.type = type;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public PersonType getType() {
        return type;
    }

    public void setType(PersonType type) {
        this.type = type;
    }
}
