package com.example.emtlab.model.views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Data
@Entity
@Subselect("SELECT * FROM public.accommodations_per_host")
@Immutable
public class AccommodationsPerHostView {

    @Id
    @Column(name = "host_id")
    private Long hostId;

    @Column(name = "num_accommodations")
    private Integer numAccommodations;
}
