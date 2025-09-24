package com.example.emtlab.service.application;

import com.example.emtlab.dto.*;
import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserApplicationService {

    Optional<DisplayUserDto> register(CreateUserDto createUserDto);

    Optional<LoginResponseDto> login(LoginUserDto loginUserDto);

    Optional<DisplayUserDto> findByUsername(String username);

    Optional<DisplayUserDto> reserveAccommodation(String username, Long accommodationId);

    Optional<DisplayUserDto> cancelAccommodation(String username, Long accommodationId);

    Optional<DisplayUserDto> bookAccommodation(String username, Long accommodationId);

    Optional<DisplayUserDto> completeStay(Long accommodationId);

    List<Accommodation> findAllReservations(String username);

    List<DisplayAccommodationDto> findAllBookings(String username);

    Optional<DisplayUserDto> bookAllReservations(String username);

    Optional<DisplayUserDto> cancelAllReservations(String username);

    Optional<DisplayUserDto> reserveAllAccommodations(String username);

    Optional<DisplayUserDto> completeStayForAllBookings(String username);

    List<User> getAllUsers();

    void deleteByUsername(String username);
}

