package com.example.emtlab.component.service.application;

import com.example.emtlab.dto.*;
import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.AccommodationCategory;
import com.example.emtlab.model.enumerations.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DtoMappingTest {

    private Country country;
    private Host host;
    private Accommodation accommodation;
    private User user;

    @BeforeEach
    void setUp() {
        country = new Country("Macedonia", "Europe");
        country.setId(1L);

        host = new Host("John", "Doe", country);
        host.setId(1L);

        accommodation = new Accommodation("Hotel", AccommodationCategory.HOTEL, host, 5);
        accommodation.setId(1L);

        user = new User("john", "pass", "John", "Doe", Role.ROLE_USER);
    }

    @Test
    void createAccommodationDtoMapping() {
        CreateAccommodationDto dto = CreateAccommodationDto.from(accommodation);
        assertThat(dto.name()).isEqualTo("Hotel");
        assertThat(dto.hostId()).isEqualTo(1L);

        Accommodation accFromDto = dto.toAccommodation(host);
        assertThat(accFromDto.getName()).isEqualTo("Hotel");
        assertThat(accFromDto.getHost()).isEqualTo(host);
    }

    @Test
    void displayAccommodationDtoMapping() {
        DisplayAccommodationDto dto = DisplayAccommodationDto.from(accommodation);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Hotel");

        Accommodation accFromDto = dto.toAccommodation(host);
        assertThat(accFromDto.getName()).isEqualTo("Hotel");
        assertThat(accFromDto.getHost()).isEqualTo(host);
    }

    @Test
    void displayAccommodationWithHostAndCountryDtoMapping() {
        DisplayAccommodationWithHostAndCountryDto dto =
                DisplayAccommodationWithHostAndCountryDto.from(accommodation);

        assertThat(dto.host()).isEqualTo(host);
        assertThat(dto.country()).isEqualTo(country);
    }

    @Test
    void createHostDtoMapping() {
        CreateHostDto dto = CreateHostDto.from(host);
        assertThat(dto.countryId()).isEqualTo(1L);

        Host hostFromDto = dto.toHost(country);
        assertThat(hostFromDto.getCountry()).isEqualTo(country);
    }

    @Test
    void displayHostDtoMapping() {
        DisplayHostDto dto = DisplayHostDto.from(host);
        assertThat(dto.id()).isEqualTo(1L);

        Host hostFromDto = dto.toHost(country);
        assertThat(hostFromDto.getCountry()).isEqualTo(country);
    }

    @Test
    void createCountryDtoMapping() {
        CreateCountryDto dto = CreateCountryDto.from(country);
        assertThat(dto.name()).isEqualTo("Macedonia");

        Country countryFromDto = dto.toCountry();
        assertThat(countryFromDto.getName()).isEqualTo("Macedonia");
    }

    @Test
    void displayCountryDtoMapping() {
        DisplayCountryDto dto = DisplayCountryDto.from(country);
        assertThat(dto.name()).isEqualTo("Macedonia");

        Country countryFromDto = dto.toCountry();
        assertThat(countryFromDto.getName()).isEqualTo("Macedonia");
    }

    @Test
    void createUserDtoMapping() {
        CreateUserDto dto = new CreateUserDto("john", "pass", "pass", "John", "Doe", Role.ROLE_USER);
        User u = dto.toUser();
        assertThat(u.getUsername()).isEqualTo("john");

        CreateUserDto wrongDto = new CreateUserDto("john", "pass", "wrong", "John", "Doe", Role.ROLE_USER);
        assertThrows(RuntimeException.class, wrongDto::toUser);
    }

    @Test
    void displayUserDtoMapping() {
        user.addReservation(accommodation);
        user.addBooking(accommodation);

        DisplayUserDto dto = DisplayUserDto.from(user);
        assertThat(dto.reservations()).contains(accommodation);
        assertThat(dto.bookings()).contains(accommodation);

        User userFromDto = dto.toUser();
        assertThat(userFromDto.getUsername()).isEqualTo("john");
    }
}
