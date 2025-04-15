package com.example.emtlab.service.application.impl;

import com.example.emtlab.dto.CreateGuestDto;
import com.example.emtlab.dto.DisplayGuestDto;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.service.application.GuestApplicationService;
import com.example.emtlab.service.domain.CountryService;
import com.example.emtlab.service.domain.GuestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GuestApplicationServiceImpl implements GuestApplicationService {

    private final GuestService guestService;
    private final CountryService countryService;

    public GuestApplicationServiceImpl(GuestService guestService, CountryService countryService) {
        this.guestService = guestService;
        this.countryService = countryService;
    }

    @Override
    public List<DisplayGuestDto> findAll() {
        return guestService.findAll().stream().map(DisplayGuestDto::from).toList();
    }

    @Override
    public Optional<DisplayGuestDto> findById(Long id) {
        return guestService.findById(id).map(DisplayGuestDto::from);
    }

    @Override
    public Optional<DisplayGuestDto> save(CreateGuestDto createGuestDto) {
        Optional<Country> country = countryService.findById(createGuestDto.countryId());

        if (country.isPresent()) {
            return guestService.save(createGuestDto.toGuest(country.get())).map(DisplayGuestDto::from);
        }
        return Optional.empty();
    }

    @Override
    public Optional<DisplayGuestDto> update(Long id, CreateGuestDto createGuestDto) {
        Optional<Country> country = countryService.findById(createGuestDto.countryId());

        return guestService.update(id,
                        createGuestDto.toGuest(country.orElse(null))
                )
                .map(DisplayGuestDto::from);
    }

    @Override
    public void deleteById(Long id) {
        guestService.deleteById(id);
    }
}
