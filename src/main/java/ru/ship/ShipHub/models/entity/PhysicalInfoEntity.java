package ru.ship.ShipHub.models.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "physical_users")
public class PhysicalInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private PersonEntity person;

    @Column(name = "address", nullable = false)
    private String address;

    public PhysicalInfoEntity(){}

    public PhysicalInfoEntity(PersonEntity person, String address) {
        this.person = person;
        this.address = address;
    }

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
