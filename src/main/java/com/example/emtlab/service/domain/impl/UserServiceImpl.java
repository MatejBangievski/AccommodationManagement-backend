package com.example.emtlab.service.domain.impl;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.Role;
import com.example.emtlab.model.exceptions.InvalidArgumentsException;
import com.example.emtlab.model.exceptions.InvalidUsernameOrPasswordException;
import com.example.emtlab.model.exceptions.PasswordsDoNotMatchException;
import com.example.emtlab.model.exceptions.UsernameAlreadyExistsException;
import com.example.emtlab.model.exceptions.InvalidUserCredentialsException;
import com.example.emtlab.repository.UserRepository;
import com.example.emtlab.service.domain.AccommodationService;
import com.example.emtlab.service.domain.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccommodationService accommodationService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, AccommodationService accommodationService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accommodationService = accommodationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                username));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                username));
    }

    @Override
    public User reserveAccommodation(String username, Long accommodationId) {
        User user = this.findByUsername(username);

        Accommodation accommodation = accommodationService.findById(accommodationId).get();
        accommodationService.reserve(accommodationId, username);

        user.addReservation(accommodation);
        return userRepository.save(user);
    }

    @Override
    public User cancelAccommodation(String username, Long accommodationId) {
        User user = this.findByUsername(username);

        Accommodation accommodation = accommodationService.findById(accommodationId).get();
        accommodationService.removeReservation(accommodationId);

        user.removeReservation(accommodation);
        return userRepository.save(user);
    }

    @Override
    public User bookAccommodation(String username, Long accommodationId) {
        User user = this.findByUsername(username);

        Accommodation accommodation = accommodationService.findById(accommodationId).get();
        accommodationService.book(accommodationId, username);

        user.removeReservation(accommodation);
        return userRepository.save(user);
    }

    @Override
    public List<Accommodation> findAllReservations(String username) {
         User user = findByUsername(username);

         return user.getAccommodationReservations().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<Accommodation> findWhereIsStaying(String username) {
        User user = findByUsername(username);

        return accommodationService.findAll()
                .stream()
                .filter(a -> a.getUserStaying() != null && a.getUserStaying().equals(user))
                .findFirst();
    }

    @Override
    public User bookAllReservations(String username) {
        User user = findByUsername(username);

        List<Accommodation> reservations = user.getAccommodationReservations().stream().collect(Collectors.toList());

        for (Accommodation acc : reservations) {
            accommodationService.book(acc.getId(), username);
            user.removeReservation(acc);
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllWithoutReservations();
    }

    @Override
    public User register(
            String username,
            String password,
            String repeatPassword,
            String name,
            String surname,
            Role userRole
    ) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty())
            throw new InvalidUsernameOrPasswordException();
        if (!password.equals(repeatPassword)) throw new PasswordsDoNotMatchException();
        if (userRepository.findByUsername(username).isPresent())
            throw new UsernameAlreadyExistsException(username);
        User user = new User(username, passwordEncoder.encode(password), name, surname, userRole);
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty())
            throw new InvalidArgumentsException();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new InvalidUserCredentialsException();

        return user;
    }
}
