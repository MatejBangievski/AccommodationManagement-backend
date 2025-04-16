package com.example.emtlab.service.domain;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;
import org.springframework.security.web.server.authentication.PreventLoginServerMaximumSessionsExceededHandler;

import java.util.List;
import java.util.Optional;

public interface AccommodationService {
    List<Accommodation> findAll();

    Optional<Accommodation> findById(Long id);

    Optional<Accommodation> save(Accommodation accommodation);

    Optional<Accommodation> update(Long id, Accommodation accommodation);

    void deleteById(Long id);

    Optional<Accommodation> reserve(Long id, String username);

    Optional<Accommodation> removeReservation(Long id);

    Optional<Accommodation> book(Long id, String username);

    Optional<Accommodation> completeStay(Long id);
}
