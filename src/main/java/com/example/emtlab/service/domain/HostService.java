package com.example.emtlab.service.domain;

import com.example.emtlab.model.domain.Guest;
import com.example.emtlab.model.domain.Host;

import java.util.List;
import java.util.Optional;

public interface HostService {
    List<Host> findAll();

    Optional<Host> findById(Long id);

    Optional<Host> save(Host host);

    Optional<Host> update(Long id, Host host);

    void deleteById(Long id);

    Optional<Host> addGuest(Long id, Guest guest);

    List<Guest> findAllGuests(Long id);
}
