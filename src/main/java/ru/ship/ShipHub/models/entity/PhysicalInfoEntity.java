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
}
