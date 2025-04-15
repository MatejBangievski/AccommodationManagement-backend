package com.example.emtlab.service.domain;

import com.example.emtlab.model.domain.Guest;

import java.util.List;
import java.util.Optional;

public interface GuestService {
    List<Guest> findAll();

    Optional<Guest> findById(Long id);

    Optional<Guest> save(Guest host);

    Optional<Guest> update(Long id, Guest host);

    void deleteById(Long id);
}
