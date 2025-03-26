package com.example.emtlab.service.application.impl;

import com.example.emtlab.dto.CreateAccommodationDto;
import com.example.emtlab.dto.DisplayAccommodationDto;
import com.example.emtlab.dto.DisplayHostDto;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.service.application.AccommodationApplicationService;
import com.example.emtlab.service.domain.AccommodationService;
import com.example.emtlab.service.domain.HostService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccommodationApplicationServiceImpl implements AccommodationApplicationService {

    private final AccommodationService accommodationService;
    private final HostService hostService;

    public AccommodationApplicationServiceImpl(AccommodationService accommodationService, HostService hostService) {
        this.accommodationService = accommodationService;
        this.hostService = hostService;
    }

    @Override
    public List<DisplayAccommodationDto> findAll() {
        return accommodationService.findAll().stream().map(DisplayAccommodationDto::from).toList();
    }

    @Override
    public Optional<DisplayAccommodationDto> findById(Long id) {
        return accommodationService.findById(id).map(DisplayAccommodationDto::from);
    }

    @Override
    public Optional<DisplayAccommodationDto> save(CreateAccommodationDto createAccommodationDto) {
        Optional<Host> host = hostService.findById(createAccommodationDto.hostId());

        if (host.isPresent()) {
            return accommodationService.save(createAccommodationDto.toAccommodation(host.get())).map(DisplayAccommodationDto::from);
        }
        return Optional.empty();
    }

    @Override
    public Optional<DisplayAccommodationDto> update(Long id, CreateAccommodationDto createAccommodationDto) {
        Optional<Host> host = hostService.findById(createAccommodationDto.hostId());

        return accommodationService.update(id,
                        createAccommodationDto.toAccommodation(host.orElse(null))
                )
                .map(DisplayAccommodationDto::from);
    }

    @Override
    public void deleteById(Long id) {
        accommodationService.deleteById(id);
    }
}
