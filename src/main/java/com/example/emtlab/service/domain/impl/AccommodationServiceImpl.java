package com.example.emtlab.service.domain.impl;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.exceptions.AccommodationAlreadyReservedException;
import com.example.emtlab.model.exceptions.AccommodationNotBookedException;
import com.example.emtlab.model.exceptions.AccommodationNotReservedException;
import com.example.emtlab.model.exceptions.UserNotFoundException;
import com.example.emtlab.repository.AccommodationRepository;
import com.example.emtlab.repository.UserRepository;
import com.example.emtlab.service.domain.AccommodationService;
import com.example.emtlab.service.domain.HostService;
import com.example.emtlab.service.domain.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final HostService hostService;

    public AccommodationServiceImpl(AccommodationRepository accommodationRepository, UserRepository userRepository, HostService hostService) {
        this.accommodationRepository = accommodationRepository;
        this.userRepository = userRepository;
        this.hostService = hostService;
    }

    @Override
    public List<Accommodation> findAll() {
        return accommodationRepository.findAll();
    }

    @Override
    public Optional<Accommodation> findById(Long id) {
        return accommodationRepository.findById(id);
    }

    // Do I need to check the enum as well?
    @Override
    public Optional<Accommodation> save(Accommodation accommodation) {
        if (accommodation.getHost() != null &&
                hostService.findById(accommodation.getHost().getId()).isPresent()) {
            return Optional.of(
                    accommodationRepository.save(new Accommodation(
                            accommodation.getName(), accommodation.getCategory(),
                            hostService.findById(accommodation.getHost().getId()).get(),
                            accommodation.getNumRooms()
                    )));
        }
        return Optional.empty();
    }


    @Override
    public Optional<Accommodation> update(Long id, Accommodation accommodation) {
        return accommodationRepository.findById(id)
                .map(existingAccommodation -> {
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
                    return accommodationRepository.save(existingAccommodation);
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
            accommodation.setUserStaying(user);

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
        accommodation.setUserStaying(null);

        return Optional.of(accommodationRepository.save(accommodation));
    }

    @Override
    public Optional<Accommodation> book(Long id, String username) {
        Accommodation accommodation = this.findById(id).get();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (accommodation.isReserved() && accommodation.getUserStaying() != null && accommodation.getUserStaying().equals(user)) {
            accommodation.setBooked(true);

            return Optional.of(accommodationRepository.save(accommodation));
        } else throw new AccommodationAlreadyReservedException(accommodation.getName());
    }

    @Override
    public Optional<Accommodation> completeStay(Long id) {
        Accommodation accommodation = this.findById(id).get();

        if (!accommodation.isBooked()) {
            throw new AccommodationNotBookedException(accommodation.getName());
        }

        accommodation.setBooked(false);
        accommodation.setBooked(false);
        accommodation.setUserStaying(null);

        return Optional.of(accommodationRepository.save(accommodation));
    }
}
