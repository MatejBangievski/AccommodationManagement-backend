package com.example.emtlab.service.application.impl;

import com.example.emtlab.dto.DisplayAccommodationWithHostAndCountryDto;
import com.example.emtlab.dto.CreateAccommodationDto;
import com.example.emtlab.dto.DisplayAccommodationDto;
import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.projections.AccommodationProjection;
import com.example.emtlab.model.views.AccommodationsPerHostView;
import com.example.emtlab.service.application.AccommodationApplicationService;
import com.example.emtlab.service.domain.AccommodationService;
import com.example.emtlab.service.domain.HostService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public Page<DisplayAccommodationDto> findAll(Pageable pageable) {
        return accommodationService.findAll(pageable)
                .map(DisplayAccommodationDto::from);
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

    @Override
    public Optional<DisplayAccommodationDto> reserve(Long id, String username) {
        Optional<Accommodation> reservedAccommodation = accommodationService.reserve(id, username);
        return reservedAccommodation.map(DisplayAccommodationDto::from);
    }

    @Override
    public Optional<DisplayAccommodationDto> removeReservation(Long id) {
        Optional<Accommodation> accommodation = accommodationService.removeReservation(id);
        return accommodation.map(DisplayAccommodationDto::from);
    }

    @Override
    public Optional<DisplayAccommodationDto> book(Long id, String username) {
        Optional<Accommodation> bookedAccommodation = accommodationService.book(id, username);
        return bookedAccommodation.map(DisplayAccommodationDto::from);
    }

    @Override
    public Optional<DisplayAccommodationDto> completeStay(Long id) {
        Optional<Accommodation> accommodation = accommodationService.completeStay(id);
        return accommodation.map(DisplayAccommodationDto::from);
    }

    @Override
    public List<AccommodationProjection> accommodationStatistics() {
        return accommodationService.accommodationStatistics();
    }

    @Override
    public void refreshMaterializedView() {
        accommodationService.refreshMaterializedView();
    }

    @Override
    public List<AccommodationsPerHostView> getAccommodationsByHost() {
        List<AccommodationsPerHostView> accommodationsByHost = accommodationService.getAccommodationsByHost();
        return accommodationsByHost;
    }

    @Override
    public Optional<DisplayAccommodationWithHostAndCountryDto> getAccommodationDetails(Long id) {
        return accommodationService.getAccommodationDetails(id);
    }
}
