package com.example.emtlab.service.application;

import com.example.emtlab.dto.CreateGuestDto;
import com.example.emtlab.dto.DisplayGuestDto;

import java.util.List;
import java.util.Optional;

public interface GuestApplicationService {
    List<DisplayGuestDto> findAll();

    Optional<DisplayGuestDto> findById(Long id);

    Optional<DisplayGuestDto> save(CreateGuestDto createGuestDto);

    Optional<DisplayGuestDto> update(Long id, CreateGuestDto createGuestDto);

    void deleteById(Long id);
}
