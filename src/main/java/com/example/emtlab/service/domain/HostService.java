package com.example.emtlab.service.domain;

import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.projections.HostProjection;
import com.example.emtlab.model.views.HostsPerCountryView;

import java.util.List;
import java.util.Optional;

public interface HostService {
    List<Host> findAll();

    Optional<Host> findById(Long id);

    Optional<Host> save(Host host);

    Optional<Host> update(Long id, Host host);

    void deleteById(Long id);

    void refreshMaterializedView();

    List<HostsPerCountryView> getHostsPerCountry();

    List<HostProjection> getNameAndSurname();
}
