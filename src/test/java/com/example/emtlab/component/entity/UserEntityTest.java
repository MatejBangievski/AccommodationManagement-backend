package com.example.emtlab.component.entity;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.AccommodationCategory;
import com.example.emtlab.model.enumerations.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    void constructorShouldInitializeFields() {
        User user = new User("john", "pass", "John", "Doe", Role.ROLE_ADMIN);

        assertThat(user.getUsername()).isEqualTo("john");
        assertThat(user.getPassword()).isEqualTo("pass");
        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.getSurname()).isEqualTo("Doe");
        assertThat(user.getRole()).isEqualTo(Role.ROLE_ADMIN);
        assertThat(user.getAccommodationsReserved()).isEmpty();
        assertThat(user.getAccommodationsBooked()).isEmpty();
    }

    @Test
    void addAndRemoveReservationShouldUpdateList() {
        User user = new User("marko", "pass", "Marko", "Markovski");
        Accommodation accommodation =
                new Accommodation("Villa Ohrid", AccommodationCategory.HOUSE, new Host(), 2);

        user.addReservation(accommodation);
        assertThat(user.getAccommodationsReserved()).containsExactly(accommodation);

        user.removeReservation(accommodation);
        assertThat(user.getAccommodationsReserved()).isEmpty();
    }

    @Test
    void addAndRemoveBookingShouldUpdateList() {
        User user = new User("iva", "pass", "Iva", "Ivanova");
        Accommodation accommodation =
                new Accommodation("Hotel Skopje", AccommodationCategory.HOTEL, new Host(), 10);

        user.addBooking(accommodation);
        assertThat(user.getAccommodationsBooked()).containsExactly(accommodation);

        user.removeBooking(accommodation);
        assertThat(user.getAccommodationsBooked()).isEmpty();
    }

    @Test
    void shouldReturnCorrectAuthorities() {
        User user = new User("ana", "pass", "Ana", "Anovska", Role.ROLE_USER);
        assertThat(user.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void shouldRespectAccountStatusFlags() {
        User user = new User("martin", "pass", "Martin", "Milevski", Role.ROLE_USER);
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }
}
