package com.example.emtlab.service.application;

import com.example.emtlab.dto.CreateAccommodationDto;
import com.example.emtlab.dto.CreateCountryDto;
import com.example.emtlab.dto.DisplayAccommodationDto;
import com.example.emtlab.dto.DisplayCountryDto;

import java.util.List;
import java.util.Optional;

public interface AccommodationApplicationService {
    List<DisplayAccommodationDto> findAll();

    Optional<DisplayAccommodationDto> findById(Long id);

    Optional<DisplayAccommodationDto> save(CreateAccommodationDto accommodationDto);

    Optional<DisplayAccommodationDto> update(Long id, CreateAccommodationDto accommodationDto);

    void deleteById(Long id);
}
