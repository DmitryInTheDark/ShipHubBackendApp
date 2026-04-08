package ru.ship.ShipHub.security;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.ship.ShipHub.models.dto.PersonDTO;
import ru.ship.ShipHub.models.entity.PersonEntity;
import ru.ship.ShipHub.util.Mapper;

import java.util.Collection;
import java.util.List;

public class PersonDetails implements UserDetails {

    private final PersonEntity person;
    @Autowired
    private Mapper mapper;

    public PersonDetails(PersonEntity person) {
        this.person = person;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return person.getPassword();
    }

    @Override
    public String getUsername() {
        return person.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public PersonDTO getBaseInfo(){
        return mapper.map(person);
    }
}
