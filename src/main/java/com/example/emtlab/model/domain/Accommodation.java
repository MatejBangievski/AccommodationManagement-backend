package com.example.emtlab.model.domain;

import com.example.emtlab.model.enumerations.AccommodationCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Host host;

    private Integer numRooms;

    @ManyToOne
    @JsonIgnore
    private User userStaying = null;

    private boolean isReserved = false;
    private boolean isBooked = false;

    public Accommodation() {
    }

    public Accommodation(String name, AccommodationCategory category, Host host, Integer numRooms) {
        this.name = name;
        this.category = category;
        this.host = host;
        this.numRooms = numRooms;
    }
}
