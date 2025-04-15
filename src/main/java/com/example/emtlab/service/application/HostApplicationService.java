package com.example.emtlab.service.application;

import com.example.emtlab.dto.CreateHostDto;
import com.example.emtlab.dto.DisplayGuestDto;
import com.example.emtlab.dto.DisplayHostDto;
import com.example.emtlab.model.domain.Guest;

import java.util.List;
import java.util.Optional;

public interface HostApplicationService {

    List<DisplayHostDto> findAll();

    Optional<DisplayHostDto> findById(Long id);

    Optional<DisplayHostDto> save(CreateHostDto createHostDto);

    Optional<DisplayHostDto> update(Long id, CreateHostDto createHostDto);

    void deleteById(Long id);

    //todo: Check if this can be done with createGuestDto - has no guest id
    Optional<DisplayHostDto> addGuest(Long id, Guest guest);

    List<DisplayGuestDto> findAllGuests(Long id);
}
