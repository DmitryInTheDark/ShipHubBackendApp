package ru.ship.ShipHub.models.entity;

import jakarta.persistence.*;
import ru.ship.ShipHub.util.PersonType;

import java.util.List;

@Entity
@Table(name = "users")
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

    @OneToOne(mappedBy = "person", orphanRemoval = true)
    private LegalInfoEntity legalInfo;

    @OneToOne(mappedBy = "person", orphanRemoval = true)
    private PhysicalInfoEntity physicalInfo;

    @OneToMany(
            targetEntity = ClaimEntity.class,
            mappedBy = "whoCreate"
    )
    private List<ClaimEntity> claims;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PersonEntity(){}

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LegalInfoEntity getLegalInfo() {
        return legalInfo;
    }

    public void setLegalInfo(LegalInfoEntity legalInfo) {
        this.legalInfo = legalInfo;
    }

    public PhysicalInfoEntity getPhysicalInfo() {
        return physicalInfo;
    }

    public void setPhysicalInfo(PhysicalInfoEntity physicalInfo) {
        this.physicalInfo = physicalInfo;
    }

    public List<ClaimEntity> getClaims() {
        return claims;
    }

    public void setClaims(List<ClaimEntity> claims) {
        this.claims = claims;
    }
}
