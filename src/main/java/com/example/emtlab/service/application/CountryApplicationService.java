package com.example.emtlab.service.application;

import com.example.emtlab.dto.CreateCountryDto;
import com.example.emtlab.dto.CreateHostDto;
import com.example.emtlab.dto.DisplayCountryDto;
import com.example.emtlab.dto.DisplayHostDto;

import java.util.List;
import java.util.Optional;

public interface CountryApplicationService {
    List<DisplayCountryDto> findAll();

    Optional<DisplayCountryDto> findById(Long id);

    Optional<DisplayCountryDto> save(CreateCountryDto createCountryDto);

    Optional<DisplayCountryDto> update(Long id, CreateCountryDto createCountryDto);

    void deleteById(Long id);

    void deleteAll();
}
