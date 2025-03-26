package com.example.emtlab.dto;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.enumerations.AccommodationCategory;

import java.util.List;
import java.util.stream.Collectors;

public record CreateAccommodationDto(
        String name,
        AccommodationCategory category,
        Long hostId,
        Integer numRooms
) {

    public static CreateAccommodationDto from(Accommodation accommodation) {
        return new CreateAccommodationDto(
                accommodation.getName(),
                accommodation.getCategory(),
                accommodation.getHost().getId(),
                accommodation.getNumRooms()
        );
    }

    public static List<CreateAccommodationDto> from(List<Accommodation> accommodations) {
        return accommodations.stream().map(CreateAccommodationDto::from).collect(Collectors.toList());
    }

    public Accommodation toAccommodation(Host host) {
        return new Accommodation(name, category, host, numRooms);
    }
}
