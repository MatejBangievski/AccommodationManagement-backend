package com.example.emtlab.dto;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.Role;

import java.util.List;

public record DisplayUserDto(
        String username,
        String name,
        String surname,
        Role role,
        List<Accommodation> reservations,
        List<Accommodation> bookings
) {

    public static DisplayUserDto from(User user) {
        return new DisplayUserDto(
                user.getUsername(),
                user.getName(),
                user.getSurname(),
                user.getRole(),
                user.getAccommodationsReserved(),
                user.getAccommodationsBooked()
        );
    }

    public User toUser() {
        return new User(username, name, surname, role.name());
    }
}
