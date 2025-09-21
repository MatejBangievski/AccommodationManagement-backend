package com.example.emtlab.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.emtlab.model.enumerations.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name = "accommodation_users")
public class User implements UserDetails {

    @Id
    private String username;

    @JsonIgnore
    private String password;

    private String name;

    private String surname;

    @OneToMany(mappedBy = "userReserved")
    @JsonIgnore
    private List<Accommodation> accommodationsReserved;

    @OneToMany(mappedBy = "userBooked")
    @JsonIgnore
    private List<Accommodation> accommodationsBooked;

    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    public User() {
    }

    public User(String username, String password, String name, String surname, Role role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.role = role;

        accommodationsReserved = new ArrayList<>();
        accommodationsBooked = new ArrayList<>();
    }

    public User(String username, String password, String name, String surname) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.role = Role.ROLE_USER;

        accommodationsReserved = new ArrayList<>();
        accommodationsBooked = new ArrayList<>();
    }

    public User(UserDetails userDetails) {
        this.username = userDetails.getUsername();
        this.password = userDetails.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList((GrantedAuthority) role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void addReservation(Accommodation accommodation) {
        accommodationsReserved.add(accommodation);
    }

    public void removeReservation(Accommodation accommodation) {
        accommodationsReserved.remove(accommodation);
    }

    public void addBooking(Accommodation accommodation) {
        accommodationsBooked.add(accommodation);
    }

    public void removeBooking(Accommodation accommodation) {
        accommodationsBooked.remove(accommodation);
    }
}
