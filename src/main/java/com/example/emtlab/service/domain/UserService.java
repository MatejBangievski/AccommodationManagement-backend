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

    User reserveAccommodation(String username, Long accommodationId);

    User cancelAccommodation(String username, Long accommodationId);

    User bookAccommodation(String username, Long accommodationId);

    User completeStay(Long accommodationId);

    List<Accommodation> findAllReservations(String username);

    List<Accommodation> findAllBookings(String username);

    User bookAllReservations(String username);

    User reserveAllAccommodations(String username);

    User cancelAllReservations(String username);

    User completeStayForAllBookings(String username);

    List<User> getAllUsers();

    void deleteByUsername(String username);
}



