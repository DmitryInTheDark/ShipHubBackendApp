package ru.ship.ShipHub.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class PersonEntity {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    @NotNull
    @NotEmpty
    @Size(min = 2, max = 100, message = "Имя должно быть длинной от 2 до 100 символов")
    private String username;

    @Column(name = "email")
    @NotNull
    @NotEmpty
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "password")
    @NotNull
    @NotEmpty
    @Size(min = 5, max = 100, message = "Пароль должен быть длинной от 2 до 100 символов")
    private String password;

    public PersonEntity(){}

    public PersonEntity(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
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
}
