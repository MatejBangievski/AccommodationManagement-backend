package com.example.emtlab.service.application;

import com.example.emtlab.dto.CreateHostDto;
import com.example.emtlab.dto.DisplayHostDto;
import com.example.emtlab.model.projections.HostProjection;
import com.example.emtlab.model.views.HostsPerCountryView;

import java.util.List;
import java.util.Optional;

public interface HostApplicationService {

    List<DisplayHostDto> findAll();

    Optional<DisplayHostDto> findById(Long id);

    Optional<DisplayHostDto> save(CreateHostDto createHostDto);

    Optional<DisplayHostDto> update(Long id, CreateHostDto createHostDto);

    void deleteById(Long id);

    void deleteAll();

    List<HostsPerCountryView> getHostsPerCountry();

    List<HostProjection> getNameAndSurname();
}
