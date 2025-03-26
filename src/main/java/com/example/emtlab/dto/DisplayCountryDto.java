package com.example.emtlab.dto;

import com.example.emtlab.model.domain.Country;

import java.util.List;

public record DisplayCountryDto(Long id, String name, String continent) {

    public static DisplayCountryDto from(Country country) {
        return new DisplayCountryDto(
                country.getId(),
                country.getName(),
                country.getContinent()
        );
    }

    public static List<DisplayCountryDto> from(List<Country> countries) {
        return countries.stream().map(DisplayCountryDto::from).toList();
    }

    public Country toCountry() {
        return new Country(name, continent);
    }
}
