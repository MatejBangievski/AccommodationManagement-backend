package com.example.emtlab.model.domain;

import com.example.emtlab.model.enumerations.AccommodationCategory;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Enumerated
    private AccommodationCategory category;
    @ManyToOne
    private Host host;

    private Integer numRooms;

    public Accommodation() {
    }

    public Accommodation(String name, AccommodationCategory category, Host host, Integer numRooms) {
        this.name = name;
        this.category = category;
        this.host = host;
        this.numRooms = numRooms;
    }
}
