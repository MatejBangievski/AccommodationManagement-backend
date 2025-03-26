package com.example.emtlab.service.domain;

import com.example.emtlab.model.domain.Accommodation;

import java.util.List;
import java.util.Optional;

public interface AccommodationService {
    List<Accommodation> findAll();

    Optional<Accommodation> findById(Long id);

    Optional<Accommodation> save(Accommodation accommodation);

    Optional<Accommodation> update(Long id, Accommodation accommodation);

    void deleteById(Long id);

}
