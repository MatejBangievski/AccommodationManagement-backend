package com.example.emtlab.service.domain.impl;

import com.example.emtlab.events.HostChangedEvent;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.enumerations.EntityChangeType;
import com.example.emtlab.model.projections.HostProjection;
import com.example.emtlab.model.views.HostsPerCountryView;
import com.example.emtlab.repository.HostRepository;
import com.example.emtlab.repository.HostsPerCountryViewRepostiory;
import com.example.emtlab.service.domain.CountryService;
import com.example.emtlab.service.domain.HostService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HostServiceImpl implements HostService {

    private final HostRepository hostRepository;
    private final HostsPerCountryViewRepostiory hostsPerCountryViewRepostiory;
    private final CountryService countryService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public HostServiceImpl(HostRepository hostRepository, HostsPerCountryViewRepostiory hostsPerCountryViewRepostiory, CountryService countryService, ApplicationEventPublisher applicationEventPublisher) {
        this.hostRepository = hostRepository;
        this.hostsPerCountryViewRepostiory = hostsPerCountryViewRepostiory;
        this.countryService = countryService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public List<Host> findAll() {
        return hostRepository.findAll();
    }

    @Override
    public Optional<Host> findById(Long id) {
        return hostRepository.findById(id);
    }

    @Override
    public Optional<Host> save(Host host) {
        Optional<Host> savedHost = Optional.empty();
        if (host.getCountry() != null && countryService.findById(host.getCountry().getId()).isPresent()) {
            savedHost =  Optional.of(
                    hostRepository.save(new Host(
                            host.getName(), host.getSurname(),
                            countryService.findById(host.getCountry().getId()).get()
                    )));
        }
        applicationEventPublisher.publishEvent(new HostChangedEvent(savedHost, EntityChangeType.CREATED));
        return savedHost;
    }

    @Override
    public Optional<Host> update(Long id, Host host) {
        return hostRepository.findById(id).map(existingHost -> {
            if (host.getName() != null) {
                existingHost.setName(host.getName());
            }
            if (host.getSurname() != null) {
                existingHost.setSurname(host.getSurname());
            }
            if (host.getCountry() != null && countryService.findById(host.getCountry().getId()).isPresent()) {
                existingHost.setCountry(countryService.findById(host.getCountry().getId()).get());
            }
            Host updatedHost = hostRepository.save(existingHost);

            applicationEventPublisher.publishEvent(new HostChangedEvent(updatedHost, EntityChangeType.UPDATED));
            return updatedHost;
        });
    }

    @Override
    public void deleteById(Long id) {
        hostRepository.findById(id).ifPresent(deletedHost -> {
            applicationEventPublisher.publishEvent(
                    new HostChangedEvent(deletedHost, EntityChangeType.DELETED)
            );
            hostRepository.deleteById(id);
        });
    }

    @Override
    public void deleteAll() {
        hostRepository.deleteAll();
    }

    @Override
    public void refreshMaterializedView() {
        hostsPerCountryViewRepostiory.refreshMaterializedView();
    }

    @Override
    public List<HostsPerCountryView> getHostsPerCountry() {
        return hostsPerCountryViewRepostiory.findAll();
    }

    @Override
    public List<HostProjection> getNameAndSurname() {
        return hostRepository.takeNameAndSurnameByProjection().stream().collect(Collectors.toList());
    }
}
