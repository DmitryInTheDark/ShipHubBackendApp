package ru.ship.ShipHub.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ship.ShipHub.util.PersonType;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class PersonEntity {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private LegalInfoEntity legalInfo;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private PhysicalInfoEntity physicalInfo;

    @Column(name = "token")
    private String token;

    @OneToMany(
            targetEntity = ClaimEntity.class,
            mappedBy = "whoCreate"
    )
    private List<ClaimEntity> claims;

    public PersonEntity(String username, String email, String password, Boolean isActive, String verificationCode, PersonType type, LegalInfoEntity info) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.verificationCode = verificationCode;
        this.type = type;
        this.legalInfo = info;
    }

    public PersonEntity(String username, String email, String password, Boolean isActive, String verificationCode, PersonType type, PhysicalInfoEntity info) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.verificationCode = verificationCode;
        this.type = type;
        this.physicalInfo = info;
    }

    public PersonEntity(String username, String email, String password, Boolean isActive, String verificationCode, PersonType type) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.verificationCode = verificationCode;
        this.type = type;
    }
}
