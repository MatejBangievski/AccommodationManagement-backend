package com.example.emtlab.model.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String continent;

    /*
        Not Required!

        1 Host -> 1 Country
        1 Country -> N Hosts

        @OneToMany(mappedBy = "country")
        private List<Host> hosts;
     */

    public Country() {
    }

    public Country(String name, String continent) {
        this.name = name;
        this.continent = continent;
    }
}
