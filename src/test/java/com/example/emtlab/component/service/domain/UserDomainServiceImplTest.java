package com.example.emtlab.component.service.domain;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.Role;
import com.example.emtlab.model.exceptions.*;
import com.example.emtlab.repository.UserRepository;
import com.example.emtlab.service.domain.AccommodationService;
import com.example.emtlab.service.domain.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserDomainServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccommodationService accommodationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Accommodation accommodation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("john", "encodedPass", "John", "Doe", Role.ROLE_USER);
        accommodation = new Accommodation("Room1", null, null, 1);
        accommodation.setId(1L);
    }

    // -----------------------------------------
    // loadUserByUsername / findByUsername
    // -----------------------------------------
    @Test
    void loadUserByUsernameShouldReturnUserIfExists() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        assertThat(userService.loadUserByUsername("john")).isEqualTo(user);
    }

    @Test
    void loadUserByUsernameShouldThrowIfNotFound() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("john"));
    }

    // -----------------------------------------
    // reserveAccommodation
    // -----------------------------------------
    @Test
    void reserveAccommodationShouldAddReservation() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(accommodationService.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationService.reserve(1L, "john")).thenReturn(Optional.of(accommodation));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.reserveAccommodation("john", 1L);

        assertThat(result.getAccommodationsReserved()).contains(accommodation);
        verify(accommodationService).reserve(1L, "john");
        verify(userRepository).save(user);
    }

    @Test
    void cancelAccommodationShouldRemoveReservation() {
        user.addReservation(accommodation);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(accommodationService.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationService.removeReservation(1L)).thenReturn(Optional.of(accommodation));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.cancelAccommodation("john", 1L);

        assertThat(result.getAccommodationsReserved()).doesNotContain(accommodation);
        verify(accommodationService).removeReservation(1L);
        verify(userRepository).save(user);
    }

    @Test
    void bookAccommodationShouldAddBookingAndRemoveReservation() {
        user.addReservation(accommodation);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(accommodationService.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationService.book(1L, "john")).thenReturn(Optional.of(accommodation));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.bookAccommodation("john", 1L);

        assertThat(result.getAccommodationsBooked()).contains(accommodation);
        assertThat(result.getAccommodationsReserved()).doesNotContain(accommodation);
        verify(accommodationService).book(1L, "john");
        verify(userRepository).save(user);
    }

    @Test
    void completeStayShouldRemoveBooking() {
        user.addBooking(accommodation);
        accommodation.setUserBooked(user);

        when(accommodationService.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationService.completeStay(1L)).thenReturn(Optional.of(accommodation));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.completeStay(1L);

        assertThat(result.getAccommodationsBooked()).doesNotContain(accommodation);
        verify(accommodationService).completeStay(1L);
        verify(userRepository).save(user);
    }

    @Test
    void completeStayShouldThrowIfAccommodationNotBooked() {
        accommodation.setBooked(false);
        when(accommodationService.findById(1L)).thenReturn(Optional.of(accommodation));

        assertThrows(RuntimeException.class, () -> userService.completeStay(1L));
    }

    // -----------------------------------------
    // findAllReservations / findAllBookings
    // -----------------------------------------
    @Test
    void findAllReservationsShouldReturnUserReservations() {
        user.addReservation(accommodation);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        assertThat(userService.findAllReservations("john")).contains(accommodation);
    }

    @Test
    void findAllBookingsShouldReturnUserBookings() {
        user.addBooking(accommodation);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        assertThat(userService.findAllBookings("john")).contains(accommodation);
    }

    // -----------------------------------------
    // bookAllReservations
    // -----------------------------------------
    @Test
    void bookAllReservationsShouldBookAll() {
        user.addReservation(accommodation);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(accommodationService.book(1L, "john")).thenReturn(Optional.of(accommodation));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.bookAllReservations("john");

        assertThat(result.getAccommodationsBooked()).contains(accommodation);
        assertThat(result.getAccommodationsReserved()).doesNotContain(accommodation);
    }

    // -----------------------------------------
    // reserveAllAccommodations
    // -----------------------------------------
    @Test
    void reserveAllAccommodationsShouldReserveFreeOnes() {
        user.getAccommodationsReserved().clear();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(accommodationService.findAllNonReserved()).thenReturn(List.of(accommodation));
        when(accommodationService.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationService.reserve(1L, "john")).thenReturn(Optional.of(accommodation));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.reserveAllAccommodations("john");

        assertThat(result.getAccommodationsReserved()).contains(accommodation);
        }

    // -----------------------------------------
    // cancelAllReservations
    // -----------------------------------------
    @Test
    void cancelAllReservationsShouldCancelAll() {
        user.getAccommodationsReserved().add(accommodation);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(accommodationService.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationService.removeReservation(1L)).thenReturn(Optional.of(accommodation));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.cancelAllReservations("john");

        assertThat(result.getAccommodationsReserved()).isEmpty();
    }

    // -----------------------------------------
    // completeStayForAllBookings
    // -----------------------------------------
    @Test
    void completeStayForAllBookingsShouldCompleteAll() {
        user.addBooking(accommodation);
        accommodation.setUserBooked(user);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(accommodationService.completeStay(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationService.findById(1L)).thenReturn(Optional.of(accommodation));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.completeStayForAllBookings("john");

        assertThat(result.getAccommodationsBooked()).isEmpty();
    }

    // -----------------------------------------
    // getAllUsers
    // -----------------------------------------
    @Test
    void getAllUsersShouldReturnAllUsers() {
        when(userRepository.findAllWithoutReservations()).thenReturn(List.of(user));

        assertThat(userService.getAllUsers()).contains(user);
    }

    // -----------------------------------------
    // register
    // -----------------------------------------
    @Test
    void registerShouldSaveUserWhenValid() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.register("john", "pass", "pass", "John", "Doe", Role.ROLE_USER);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void registerShouldThrowIfUsernameExists() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.register("john", "pass", "pass", "John", "Doe", Role.ROLE_USER));
    }

    @Test
    void registerShouldThrowIfPasswordsDoNotMatch() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        assertThrows(PasswordsDoNotMatchException.class,
                () -> userService.register("john", "pass1", "pass2", "John", "Doe", Role.ROLE_USER));
    }

    @Test
    void registerShouldThrowIfUsernameOrPasswordInvalid() {
        assertThrows(InvalidUsernameOrPasswordException.class,
                () -> userService.register("", "pass", "pass", "John", "Doe", Role.ROLE_USER));
        assertThrows(InvalidUsernameOrPasswordException.class,
                () -> userService.register("john", "", "pass", "John", "Doe", Role.ROLE_USER));
    }

    // -----------------------------------------
    // login
    // -----------------------------------------
    @Test
    void loginShouldReturnUserWhenCredentialsCorrect() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(true);

        assertThat(userService.login("john", "pass")).isEqualTo(user);
    }

    @Test
    void loginShouldThrowIfInvalidArguments() {
        assertThrows(InvalidArgumentsException.class, () -> userService.login("", "pass"));
        assertThrows(InvalidArgumentsException.class, () -> userService.login("john", ""));
    }

    @Test
    void loginShouldThrowIfUserNotFound() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.login("john", "pass"));
    }

    @Test
    void loginShouldThrowIfPasswordIncorrect() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(false);

        assertThrows(InvalidUserCredentialsException.class, () -> userService.login("john", "pass"));
    }
}