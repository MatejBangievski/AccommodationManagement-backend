package com.example.emtlab.service.domain;

import com.example.emtlab.dto.DisplayAccommodationWithHostAndCountryDto;
import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.projections.AccommodationProjection;
import com.example.emtlab.model.views.AccommodationsPerHostView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AccommodationService {
    List<Accommodation> findAll();

    List<Accommodation> findAllNonReserved();

    Page<Accommodation> findAll(Pageable pageable);

    Optional<Accommodation> findById(Long id);

    Optional<Accommodation> save(Accommodation accommodation);

    Optional<Accommodation> update(Long id, Accommodation accommodation);

    void deleteById(Long id);

    void deleteAll();

    Optional<Accommodation> reserve(Long id, String username);

    Optional<Accommodation> removeReservation(Long id);

    Optional<Accommodation> book(Long id, String username);

    Optional<Accommodation> completeStay(Long id);

    List<AccommodationProjection> accommodationStatistics();

    void refreshMaterializedView();

    List<AccommodationsPerHostView> getAccommodationsByHost();

    Optional<DisplayAccommodationWithHostAndCountryDto> getAccommodationDetails(Long id);
}


