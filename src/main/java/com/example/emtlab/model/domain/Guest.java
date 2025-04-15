package com.example.emtlab.model.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;

    @ManyToOne
    private Country country;

    @ManyToMany(mappedBy = "historyOfGuests")
    List<Host> historyOfHosts;
    public Guest() {
        historyOfHosts = new ArrayList<>();
    }

    public Guest(String name, String surname, Country country) {
        this.name = name;
        this.surname = surname;
        this.country = country;
        historyOfHosts = new ArrayList<>();
    }
}
