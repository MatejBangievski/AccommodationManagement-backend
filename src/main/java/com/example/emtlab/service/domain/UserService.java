package com.example.emtlab.service.domain;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User register(String username, String password, String repeatPassword, String name, String surname, Role role);

    User login(String username, String password);

    User findByUsername(String username);

    User reserveAccommodation (String username, Long accommodationId);

    User cancelAccommodation (String username, Long accommodationId);

    User bookAccommodation (String username, Long accommodationId);

    List<Accommodation> findAllReservations(String username);

    User bookAllReservations(String username);

    List<User> getAllUsers();
}
