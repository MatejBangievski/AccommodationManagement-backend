package com.example.emtlab.service.domain.impl;

import com.example.emtlab.dto.DisplayAccommodationWithHostAndCountryDto;
import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.exceptions.*;
import com.example.emtlab.model.projections.AccommodationProjection;
import com.example.emtlab.model.views.AccommodationsPerHostView;
import com.example.emtlab.repository.AccommodationRepository;
import com.example.emtlab.repository.AccommodationsPerHostViewRepository;
import com.example.emtlab.repository.UserRepository;
import com.example.emtlab.service.domain.AccommodationService;
import com.example.emtlab.service.domain.HostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationsPerHostViewRepository accommodationsPerHostViewRepository;
    private final UserRepository userRepository;
    private final HostService hostService;

    public AccommodationServiceImpl(AccommodationRepository accommodationRepository, AccommodationsPerHostViewRepository accommodationsPerHostViewRepository, UserRepository userRepository, HostService hostService) {
        this.accommodationRepository = accommodationRepository;
        this.accommodationsPerHostViewRepository = accommodationsPerHostViewRepository;
        this.userRepository = userRepository;
        this.hostService = hostService;
    }

    @Override
    public List<Accommodation> findAll() {
        return accommodationRepository.findAll();
    }

    @Override
    public List<Accommodation> findAllNonReserved() {
        return findAll().stream()
                .filter(a -> !a.isReserved())
                .collect(Collectors.toList());
    }

    @Override
    public Page<Accommodation> findAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable);
    }

    @Override
    public Optional<Accommodation> findById(Long id) {
        return accommodationRepository.findById(id);
    }

    // Do I need to check the enum as well?
    @Override
    public Optional<Accommodation> save(Accommodation accommodation) {
        Optional<Accommodation> savedAccommodation = Optional.empty();

        if (accommodation.getHost() != null &&
                hostService.findById(accommodation.getHost().getId()).isPresent()) {
            savedAccommodation = Optional.of(accommodationRepository.save(
                    new Accommodation(accommodation.getName(), accommodation.getCategory(),
                            hostService.findById(accommodation.getHost().getId()).get(),
                            accommodation.getNumRooms()
                    )));
        }
        return savedAccommodation;
    }


    @Override
    public Optional<Accommodation> update(Long id, Accommodation accommodation) {
        return accommodationRepository.findById(id).map(existingAccommodation -> {
            if (accommodation.getName() != null) {
                existingAccommodation.setName(accommodation.getName());
            }
            if (accommodation.getCategory() != null) {
                existingAccommodation.setCategory(accommodation.getCategory());
            }
            if (accommodation.getHost() != null && hostService.findById(accommodation.getHost().getId()).isPresent()) {
                existingAccommodation.setHost(hostService.findById(accommodation.getHost().getId()).get());
            }
            if (accommodation.getNumRooms() != null) {
                existingAccommodation.setNumRooms(accommodation.getNumRooms());
            }
            Accommodation updatedAccommodation = accommodationRepository.save(existingAccommodation);
            return updatedAccommodation;
        });
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
    }

    @Override
    public Optional<Accommodation> reserve(Long id, String username) {
        Accommodation accommodation = this.findById(id).get();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (!accommodation.isReserved()) {
            accommodation.setReserved(true);
            accommodation.setUserReserved(user);

            return Optional.of(accommodationRepository.save(accommodation));
        } else throw new AccommodationAlreadyReservedException(accommodation.getName());
    }

    @Override
    public Optional<Accommodation> removeReservation(Long id) {
        Accommodation accommodation = this.findById(id).get();

        if (!accommodation.isReserved()) {
            throw new AccommodationNotReservedException(accommodation.getName());
        }

        accommodation.setReserved(false);
        accommodation.setUserReserved(null);

        return Optional.of(accommodationRepository.save(accommodation));
    }

    @Override
    public Optional<Accommodation> book(Long id, String username) {
        Accommodation accommodation = this.findById(id).get();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (accommodation.isReserved() && accommodation.getUserReserved().equals(user)
                && !accommodation.isBooked()) {

            accommodation.setBooked(true);
            accommodation.setUserBooked(user);

            accommodation.setReserved(false);
            accommodation.setUserReserved(null);

            return Optional.of(accommodationRepository.save(accommodation));
        } else throw new AccommodationAlreadyBookedException(accommodation.getName());
    }

    @Override
    public Optional<Accommodation> completeStay(Long id) {
        Accommodation accommodation = this.findById(id).get();

        if (!accommodation.isBooked()) {
            throw new AccommodationNotBookedException(accommodation.getName());
        }

        accommodation.setBooked(false);
        accommodation.setUserBooked(null);

        return Optional.of(accommodationRepository.save(accommodation));
    }

    @Override
    public List<AccommodationProjection> accommodationStatistics() {
        return accommodationRepository.takeCategoryAndCountByProjection().stream().collect(Collectors.toList());
    }

    @Override
    public void refreshMaterializedView() {
        accommodationsPerHostViewRepository.refreshMaterializedView();
    }

    @Override
    public List<AccommodationsPerHostView> getAccommodationsByHost() {
        return accommodationsPerHostViewRepository.findAll();
    }

    @Override
    public Optional<DisplayAccommodationWithHostAndCountryDto> getAccommodationDetails(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Accommodation not found"));

        return Optional.of(DisplayAccommodationWithHostAndCountryDto.from(accommodation));
    }
}
