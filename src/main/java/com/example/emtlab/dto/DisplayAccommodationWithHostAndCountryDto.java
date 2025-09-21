package com.example.emtlab.dto;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.AccommodationCategory;

public record DisplayAccommodationWithHostAndCountryDto(
        Long id,
        String name,
        AccommodationCategory category,
        Integer numRooms,
        boolean isReserved,
        boolean isBooked,
        User userReserved,
        User userBooked,
        Host host,
        Country country
) {
    public static DisplayAccommodationWithHostAndCountryDto from(Accommodation accommodation) {
        return new DisplayAccommodationWithHostAndCountryDto(
                accommodation.getId(),
                accommodation.getName(),
                accommodation.getCategory(),
                accommodation.getNumRooms(),
                accommodation.isReserved(),
                accommodation.isBooked(),
                accommodation.getUserReserved(),
                accommodation.getUserBooked(),
                accommodation.getHost(),
                accommodation.getHost().getCountry()
        );
    }
}
