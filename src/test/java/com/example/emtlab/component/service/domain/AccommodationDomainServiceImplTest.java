package com.example.emtlab.component.service.domain;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.AccommodationCategory;
import com.example.emtlab.model.exceptions.*;
import com.example.emtlab.repository.AccommodationRepository;
import com.example.emtlab.repository.AccommodationsPerHostViewRepository;
import com.example.emtlab.repository.UserRepository;
import com.example.emtlab.service.domain.HostService;
import com.example.emtlab.service.domain.impl.AccommodationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AccommodationDomainServiceImplTest {
    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AccommodationsPerHostViewRepository accommodationsPerHostViewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HostService hostService;

    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    private Host host;
    private Accommodation accommodation;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        host = new Host("John", "Doe", null);
        host.setId(1L);

        accommodation = new Accommodation("Hotel Test", AccommodationCategory.HOTEL, host, 10);
        accommodation.setId(1L);

        user = new User("john", "password", "John", "Doe");
    }


    // ===========================
    // Basic CRUD tests
    // ===========================

    @Test
    void findAllShouldReturnAllAccommodations() {
        when(accommodationRepository.findAll()).thenReturn(List.of(accommodation));
        List<Accommodation> result = accommodationService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Hotel Test");
    }

    @Test
    void findAllNonReservedShouldFilterReservedAccommodations() {
        accommodation.setReserved(true);
        when(accommodationRepository.findAll()).thenReturn(List.of(accommodation));
        List<Accommodation> result = accommodationService.findAllNonReserved();
        assertThat(result).isEmpty();
    }

    @Test
    void findByIdShouldReturnAccommodation() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        Optional<Accommodation> result = accommodationService.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Hotel Test");
    }

    @Test
    void saveShouldReturnSavedAccommodationWhenHostExists() {
        when(hostService.findById(1L)).thenReturn(Optional.of(host));
        when(accommodationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Accommodation> result = accommodationService.save(accommodation);

        assertThat(result).isPresent();
        assertThat(result.get().getHost()).isEqualTo(host);
        verify(accommodationRepository, times(1)).save(any());
    }

    @Test
    void saveShouldReturnEmptyWhenHostDoesNotExist() {
        when(hostService.findById(anyLong())).thenReturn(Optional.empty());
        Optional<Accommodation> result = accommodationService.save(accommodation);
        assertThat(result).isEmpty();
        verify(accommodationRepository, never()).save(any());
    }

    @Test
    void updateShouldModifyExistingAccommodation() {
        Accommodation update = new Accommodation("Updated Hotel", AccommodationCategory.HOTEL, host, 10);
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(hostService.findById(host.getId())).thenReturn(Optional.of(host));
        when(accommodationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Accommodation> result = accommodationService.update(1L, update);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Updated Hotel");
        assertThat(result.get().getNumRooms()).isEqualTo(10);
    }

    @Test
    void deleteByIdShouldCallRepositoryDelete() {
        doNothing().when(accommodationRepository).deleteById(1L);
        accommodationService.deleteById(1L);
        verify(accommodationRepository, times(1)).deleteById(1L);
    }

    // ===========================
    // Reservation tests
    // ===========================

    @Test
    void reserveShouldSetReservedAndUser() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(accommodationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Accommodation> result = accommodationService.reserve(1L, "john");

        assertThat(result).isPresent();
        assertThat(result.get().isReserved()).isTrue();
        assertThat(result.get().getUserReserved()).isEqualTo(user);
    }

    @Test
    void reserveShouldThrowIfAlreadyReserved() {
        accommodation.setReserved(true);
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        assertThrows(AccommodationAlreadyReservedException.class,
                () -> accommodationService.reserve(1L, "john"));
    }

    @Test
    void reserveShouldThrowIfUserNotFound() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> accommodationService.reserve(1L, "john"));
    }

    @Test
    void removeReservationShouldUnsetReservedAndUser() {
        accommodation.setReserved(true);
        accommodation.setUserReserved(user);
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Accommodation> result = accommodationService.removeReservation(1L);

        assertThat(result).isPresent();
        assertThat(result.get().isReserved()).isFalse();
        assertThat(result.get().getUserReserved()).isNull();
    }

    @Test
    void removeReservationShouldThrowIfNotReserved() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        assertThrows(AccommodationNotReservedException.class,
                () -> accommodationService.removeReservation(1L));
    }

    // ===========================
    // Booking tests
    // ===========================

    @Test
    void bookShouldSetBookedAndUnsetReservation() {
        accommodation.setReserved(true);
        accommodation.setUserReserved(user);
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(accommodationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Accommodation> result = accommodationService.book(1L, "john");

        assertThat(result).isPresent();
        assertThat(result.get().isBooked()).isTrue();
        assertThat(result.get().getUserBooked()).isEqualTo(user);
        assertThat(result.get().isReserved()).isFalse();
        assertThat(result.get().getUserReserved()).isNull();
    }

    @Test
    void bookShouldThrowIfAlreadyBooked() {
        accommodation.setBooked(true);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));

        assertThrows(AccommodationAlreadyBookedException.class,
                () -> accommodationService.book(1L, "john"));
    }

    @Test
    void bookShouldThrowIfUserMismatch() {
        accommodation.setReserved(true);
        User otherUser = new User("jane", "pass", "Jane", "Doe");
        accommodation.setUserReserved(otherUser);
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        assertThrows(AccommodationAlreadyBookedException.class,
                () -> accommodationService.book(1L, "john"));
    }

    @Test
    void completeStayShouldUnsetBookedAndUser() {
        accommodation.setBooked(true);
        accommodation.setUserBooked(user);
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Accommodation> result = accommodationService.completeStay(1L);

        assertThat(result).isPresent();
        assertThat(result.get().isBooked()).isFalse();
        assertThat(result.get().getUserBooked()).isNull();
    }

    @Test
    void completeStayShouldThrowIfNotBooked() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        assertThrows(AccommodationNotBookedException.class,
                () -> accommodationService.completeStay(1L));
    }

    // ===========================
    // Additional methods
    // ===========================

    @Test
    void getAccommodationDetailsShouldReturnDto() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        assertThat(accommodationService.getAccommodationDetails(1L)).isPresent();
    }

    @Test
    void getAccommodationDetailsShouldThrowIfNotFound() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> accommodationService.getAccommodationDetails(1L));
    }

    @Test
    void refreshMaterializedViewShouldCallRepository() {
        doNothing().when(accommodationsPerHostViewRepository).refreshMaterializedView();
        accommodationService.refreshMaterializedView();
        verify(accommodationsPerHostViewRepository, times(1)).refreshMaterializedView();
    }
}
