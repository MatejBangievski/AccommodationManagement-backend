package com.example.emtlab.service.application.impl;

import com.example.emtlab.dto.CreateUserDto;
import com.example.emtlab.dto.DisplayUserDto;
import com.example.emtlab.dto.LoginUserDto;
import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.service.application.UserApplicationService;
import com.example.emtlab.service.domain.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserService userService;

    public UserApplicationServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<DisplayUserDto> register(CreateUserDto createUserDto) {
        User user = userService.register(
                createUserDto.username(),
                createUserDto.password(),
                createUserDto.repeatPassword(),
                createUserDto.name(),
                createUserDto.surname(),
                createUserDto.role()
        );
        return Optional.of(DisplayUserDto.from(user));
    }

    @Override
    public Optional<DisplayUserDto> login(LoginUserDto loginUserDto) {
        return Optional.of(DisplayUserDto.from(userService.login(
                loginUserDto.username(),
                loginUserDto.password()
        )));
    }

    @Override
    public Optional<DisplayUserDto> findByUsername(String username) {
        return Optional.of(DisplayUserDto.from(userService.findByUsername(username)));
    }

    @Override
    public Optional<DisplayUserDto> reserveAccommodation(String username, Long accommodationId) {
        User user = userService.reserveAccommodation(username, accommodationId);
        return Optional.of(DisplayUserDto.from(user));
    }

    @Override
    public Optional<DisplayUserDto> cancelAccommodation(String username, Long accommodationId) {
        User user = userService.cancelAccommodation(username, accommodationId);
        return Optional.of(DisplayUserDto.from(user));
    }

    @Override
    public Optional<DisplayUserDto> bookAccommodation(String username, Long accommodationId) {
        User user = userService.bookAccommodation(username, accommodationId);
        return Optional.of(DisplayUserDto.from(user));
    }

    @Override
    public List<Accommodation> findAllReservations(String username) {
        return userService.findAllReservations(username);
    }

    @Override
    public Optional<DisplayUserDto> bookAllReservations(String username) {
        User user = userService.bookAllReservations(username);
        return Optional.of(DisplayUserDto.from(user));
    }

    @Override
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
