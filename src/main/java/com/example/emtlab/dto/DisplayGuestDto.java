package com.example.emtlab.dto;

import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Guest;

import java.util.List;
import java.util.stream.Collectors;

public record DisplayGuestDto (
        Long id,
        String name,
        String surname,
        Long countryId
){
    public static DisplayGuestDto from(Guest guest) {
        return new DisplayGuestDto(
                guest.getId(),
                guest.getName(),
                guest.getSurname(),
                guest.getCountry().getId()
        );
    }

    public static List<DisplayGuestDto> from(List<Guest> guests) {
        return guests.stream().map(DisplayGuestDto::from).collect(Collectors.toList());
    }

    public Guest toGuest(Country country) {
        return new Guest(name, surname, country);
    }
}
