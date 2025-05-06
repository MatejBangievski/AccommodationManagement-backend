package com.example.emtlab.service.application;

import com.example.emtlab.dto.CreateUserDto;
import com.example.emtlab.dto.DisplayUserDto;
import com.example.emtlab.dto.LoginUserDto;
import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserApplicationService {

    Optional<DisplayUserDto> register(CreateUserDto createUserDto);

    Optional<DisplayUserDto> login(LoginUserDto loginUserDto);

    Optional<DisplayUserDto> findByUsername(String username);

    Optional<DisplayUserDto> reserveAccommodation (String username, Long accommodationId);

    Optional<DisplayUserDto> cancelAccommodation (String username, Long accommodationId);

    Optional<DisplayUserDto> bookAccommodation (String username, Long accommodationId);

    List<Accommodation> findAllReservations(String username);

    Optional<DisplayUserDto> bookAllReservations(String username);

    List<User> getAllUsers();
}

