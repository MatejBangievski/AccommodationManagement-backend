package com.example.emtlab.service.application.impl;

import com.example.emtlab.dto.CreateHostDto;
import com.example.emtlab.dto.DisplayHostDto;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.service.application.HostApplicationService;
import com.example.emtlab.service.domain.CountryService;
import com.example.emtlab.service.domain.HostService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HostApplicationServiceImpl implements HostApplicationService {

    private final HostService hostService;
    private final CountryService countryService;

    public HostApplicationServiceImpl(HostService hostService, CountryService countryService) {
        this.hostService = hostService;
        this.countryService = countryService;
    }

    @Override
    public List<DisplayHostDto> findAll() {
        return hostService.findAll().stream().map(DisplayHostDto::from).toList();
    }

    @Override
    public Optional<DisplayHostDto> findById(Long id) {
        return hostService.findById(id).map(DisplayHostDto::from);
    }

    @Override
    public Optional<DisplayHostDto> save(CreateHostDto createHostDto) {
        Optional<Country> country = countryService.findById(createHostDto.countryId());

        if (country.isPresent()) {
            return hostService.save(createHostDto.toHost(country.get())).map(DisplayHostDto::from);
        }
        return Optional.empty();
    }

    @Override
    public Optional<DisplayHostDto> update(Long id, CreateHostDto createHostDto) {
        Optional<Country> country = countryService.findById(createHostDto.countryId());

        return hostService.update(id,
                        createHostDto.toHost(country.orElse(null))
                )
                .map(DisplayHostDto::from);
    }

    @Override
    public void deleteById(Long id) {
        hostService.deleteById(id);
    }
}
