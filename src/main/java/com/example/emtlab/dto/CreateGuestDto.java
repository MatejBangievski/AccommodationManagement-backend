package com.example.emtlab.dto;

import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Guest;

import java.util.List;
import java.util.stream.Collectors;

public record CreateGuestDto (String name, String surname, Long countryId) {

    public static CreateGuestDto from(Guest guest) {
        return new CreateGuestDto(
                guest.getName(),
                guest.getSurname(),
                guest.getCountry().getId()
        );
    }

    public static List<CreateGuestDto> from(List<Guest> guests) {
        return guests.stream().map(CreateGuestDto::from).collect(Collectors.toList());
    }

    public Guest toGuest(Country country) {
        return new Guest(name, surname, country);
    }
}
