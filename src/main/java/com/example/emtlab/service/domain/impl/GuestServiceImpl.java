package com.example.emtlab.service.domain.impl;

import com.example.emtlab.model.domain.Guest;
import com.example.emtlab.repository.GuestRepository;
import com.example.emtlab.service.domain.CountryService;
import com.example.emtlab.service.domain.GuestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;
    private final CountryService countryService;

    public GuestServiceImpl(GuestRepository guestRepository, CountryService countryService) {
        this.guestRepository = guestRepository;
        this.countryService = countryService;
    }

    @Override
    public List<Guest> findAll() {
        return guestRepository.findAll();
    }

    @Override
    public Optional<Guest> findById(Long id) {
        return guestRepository.findById(id);
    }

    @Override
    public Optional<Guest> save(Guest guest) {
        if (guest.getCountry() != null &&
                countryService.findById(guest.getCountry().getId()).isPresent()) {
            return Optional.of(
                    guestRepository.save(new Guest(
                            guest.getName(), guest.getSurname(),
                            countryService.findById(guest.getCountry().getId()).get()
                    )));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Guest> update(Long id, Guest guest) {
        return guestRepository.findById(id).map(existingHost -> {
            if (guest.getName() != null) {
                existingHost.setName(guest.getName());
            }
            if (guest.getSurname() != null) {
                existingHost.setSurname(guest.getSurname());
            }
            if (guest.getCountry() != null && countryService.findById(guest.getCountry().getId()).isPresent()) {
                existingHost.setCountry(countryService.findById(guest.getCountry().getId()).get());
            }
            return guestRepository.save(existingHost);
        });
    }

    @Override
    public void deleteById(Long id) {
        guestRepository.deleteById(id);
    }
}
