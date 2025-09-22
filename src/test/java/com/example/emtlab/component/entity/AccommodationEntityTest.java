package com.example.emtlab.component.entity;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.AccommodationCategory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccommodationEntityTest {

    @Test
    void constructorShouldInitializeFields() {
        Country country = new Country("England", "Europe");
        Host host = new Host("John", "Doe", country);
        Accommodation accommodation =
                new Accommodation("Hotel Alexander Palace", AccommodationCategory.HOTEL, host, 20);

        assertThat(accommodation.getName()).isEqualTo("Hotel Alexander Palace");
        assertThat(accommodation.getCategory()).isEqualTo(AccommodationCategory.HOTEL);
        assertThat(accommodation.getHost()).isEqualTo(host);
        assertThat(accommodation.getNumRooms()).isEqualTo(20);
        assertThat(accommodation.isReserved()).isFalse();
        assertThat(accommodation.isBooked()).isFalse();
    }

    @Test
    void shouldAllowAssigningReservedAndBookedUsers() {
        Country country = new Country("Macedonia", "Europe");
        Host host = new Host("Jane", "Doe", country);
        Accommodation accommodation =
                new Accommodation("Jane's Apartments", AccommodationCategory.APARTMENT, host, 5);

        User reservedUser = new User("marko11", "pass", "Marko", "Markovski");
        User bookedUser = new User("iva", "pass", "Iva", "Ivanova");

        accommodation.setUserReserved(reservedUser);
        accommodation.setReserved(true);
        accommodation.setUserBooked(bookedUser);
        accommodation.setBooked(true);

        assertThat(accommodation.getUserReserved()).isEqualTo(reservedUser);
        assertThat(accommodation.getUserBooked()).isEqualTo(bookedUser);
        assertThat(accommodation.isReserved()).isTrue();
        assertThat(accommodation.isBooked()).isTrue();
    }
}
