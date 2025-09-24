package com.example.emtlab.integration.controller;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.enumerations.AccommodationCategory;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.Role;
import com.example.emtlab.service.domain.AccommodationService;
import com.example.emtlab.service.domain.CountryService;
import com.example.emtlab.service.domain.HostService;
import com.example.emtlab.service.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends AbstractControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AccommodationService accommodationService;

    @Autowired
    private HostService hostService;

    @Autowired
    private CountryService countryService;

    private User user;
    private Host host;
    private Country country;
    private Accommodation accommodation;

    @BeforeEach
    void setUp() {
        userService.deleteAll();
        accommodationService.deleteAll();
        hostService.deleteAll();
        countryService.deleteAll();

        user = userService.register("john", "pass", "pass", "John", "Doe", Role.ROLE_ADMIN);

        country = countryService.save(new Country("Atlantis", "Africa")).get();
        host = hostService.save(new Host("Hoster", "Surname", country)).get();

        accommodation = accommodationService.save(
                new Accommodation("HotelX", AccommodationCategory.HOTEL, host, 2)).get();
    }

   @Test
    void register_valid() throws Exception {
        String json = """
            { "username":"anna", "password":"123", "repeatPassword":"123",
              "name":"Anna", "surname":"Smith", "role":"ROLE_USER" }
        """;

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("anna"));
    }

    @Test
    void register_passwordsDoNotMatch() throws Exception {
        String json = """
        { "username":"mark", "password":"123", "repeatPassword":"456",
          "name":"Mark", "surname":"Mismatch", "role":"ROLE_USER" }
    """;

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_invalidArguments() throws Exception {
        String json = """
        { "username":"", "password":"123", "repeatPassword":"123",
          "name":"No", "surname":"User", "role":"" }
    """;

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_duplicateUsername() throws Exception {
        String json = """
            { "username":"john", "password":"123", "repeatPassword":"123",
              "name":"John", "surname":"Dup", "role":"ROLE_USER" }
        """;

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict()); // 409
    }

    @Test
    void login_valid() throws Exception {
        String json = """
            { "username":"john", "password":"pass" }
        """;

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void login_invalidCredentials() throws Exception {
        String json = """
            { "username":"john", "password":"wrong" }
        """;

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByUsername_existing() throws Exception {
        mockMvc.perform(get("/api/user/{username}", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));
    }

    @Test
    void findByUsername_nonExisting() throws Exception {
        mockMvc.perform(get("/api/user/{username}", "ghost"))
//                .andExpect(status().isNotFound()); jwt - access denied
                .andExpect(status().isForbidden());
    }

    @Test
    void reserveAccommodation_valid() throws Exception {
        mockMvc.perform(post("/api/user/{username}/reserve/{id}", "john", accommodation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));
    }

    @Test
    void reserveAccommodation_invalidUser() throws Exception {
        mockMvc.perform(post("/api/user/{username}/reserve/{id}", "ghost", accommodation.getId()))
//                .andExpect(status().isNotFound()); jwt - access denied
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelAccommodation_validReservation() throws Exception {
        Accommodation a = new Accommodation("TestHotel", AccommodationCategory.HOTEL, host, 2);
        a = accommodationService.save(a).get();
        a = accommodationService.reserve(a.getId(), user.getUsername()).get();

        mockMvc.perform(post("/api/user/{username}/cancel/{accommodationId}", user.getUsername(), a.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void cancelAccommodation_nonExistingReservation() throws Exception {
        mockMvc.perform(post("/api/user/{username}/cancel/{accommodationId}", user.getUsername(), 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelAccommodation_notReserved() throws Exception {
        Accommodation a = new Accommodation("HotelX", AccommodationCategory.HOTEL, host, 2);
        a = accommodationService.save(a).get();

        mockMvc.perform(post("/api/user/{username}/cancel/{accommodationId}", user.getUsername(), a.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookAccommodation_validReservation() throws Exception {
        Accommodation a = new Accommodation("HotelBook", AccommodationCategory.HOTEL, host, 2);
        a = accommodationService.save(a).get();
        a = accommodationService.reserve(a.getId(), user.getUsername()).get();

        mockMvc.perform(post("/api/user/{username}/book/{accommodationId}", user.getUsername(), a.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void bookAccommodation_nonExistingReservation() throws Exception {
        mockMvc.perform(post("/api/user/{username}/book/{accommodationId}", user.getUsername(), 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookAccommodation_notReserved() throws Exception {
        Accommodation a = new Accommodation("HotelNoReserve", AccommodationCategory.HOTEL, host, 2);
        a = accommodationService.save(a).get();

        mockMvc.perform(post("/api/user/{username}/book/{accommodationId}", user.getUsername(), a.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookAccommodation_alreadyBooked() throws Exception {
        Accommodation a = new Accommodation("HotelBooked", AccommodationCategory.HOTEL, host, 2);
        a = accommodationService.save(a).get();
        a = accommodationService.reserve(a.getId(), user.getUsername()).get();
        a = accommodationService.book(a.getId(), user.getUsername()).get(); // already booked

        mockMvc.perform(post("/api/user/{username}/book/{accommodationId}", user.getUsername(), a.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void completeStay_valid() throws Exception {
        Accommodation a = new Accommodation("HotelStay", AccommodationCategory.HOTEL, host, 2);
        a = accommodationService.save(a).get();
        a = accommodationService.reserve(a.getId(), user.getUsername()).get();
        a = accommodationService.book(a.getId(), user.getUsername()).get();

        mockMvc.perform(get("/api/user/completeStay/{accommodationId}", a.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void completeStay_invalid() throws Exception {
        mockMvc.perform(get("/api/user/completeStay/{accommodationId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void completeStay_notBooked() throws Exception {
        Accommodation a = new Accommodation("HotelStayNotBooked", AccommodationCategory.HOTEL, host, 2);
        a = accommodationService.save(a).get();
        a = accommodationService.reserve(a.getId(), user.getUsername()).get();

        mockMvc.perform(get("/api/user/completeStay/{accommodationId}", a.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookAllReservations_allAvailable() throws Exception {
        Accommodation a1 = new Accommodation("Hotel1", AccommodationCategory.HOTEL, host, 2);
        Accommodation a2 = new Accommodation("Hotel2", AccommodationCategory.HOTEL, host, 2);
        a1 = accommodationService.save(a1).get();
        a2 = accommodationService.save(a2).get();
        accommodationService.reserve(a1.getId(), user.getUsername());
        accommodationService.reserve(a2.getId(), user.getUsername());

        mockMvc.perform(post("/api/user/{username}/bookAll", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void bookAllReservations_partialUnavailable() throws Exception {
        User anotherUser = userService.register("bob", "pass", "pass", "Bob", "Ross", Role.ROLE_USER);

        Accommodation a1 = new Accommodation("HotelB3", AccommodationCategory.HOTEL, host, 2);
        Accommodation a2 = new Accommodation("HotelB4", AccommodationCategory.HOTEL, host, 2);
        a1 = accommodationService.save(a1).get();
        a2 = accommodationService.save(a2).get();

        accommodationService.reserve(a1.getId(), anotherUser.getUsername());
        accommodationService.book(a1.getId(), anotherUser.getUsername());

        accommodationService.reserve(a2.getId(), user.getUsername());

        mockMvc.perform(post("/api/user/{username}/bookAll", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.bookings", hasSize(1)));
    }

    @Test
    void bookAllReservations_invalid() throws Exception {
        mockMvc.perform(post("/api/user/{username}/bookAll", "ghost"))
//                .andExpect(status().isNotFound()); jwt - access denied
                .andExpect(status().isForbidden());
    }

    @Test
    void reserveAllAccommodations_allAvailable() throws Exception {
        Accommodation a1 = new Accommodation("HotelR1", AccommodationCategory.HOTEL, host, 2);
        a1 = accommodationService.save(a1).get();

        mockMvc.perform(post("/api/user/{username}/reserveAll", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.reservations", hasSize(2)));
    }

    @Test
    void reserveAllAccommodations_partialUnavailable() throws Exception {
        User anotherUser = userService.register("alice", "pass", "pass", "Alice", "Wonderland", Role.ROLE_ADMIN);

        Accommodation a1 = new Accommodation("HotelR3", AccommodationCategory.HOTEL, host, 2);
        a1 = accommodationService.save(a1).get();
        accommodationService.reserve(a1.getId(), anotherUser.getUsername()); // reserved by another user

        mockMvc.perform(post("/api/user/{username}/reserveAll", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.reservations", hasSize(1)));
    }

    @Test
    void reserveAllAccommodations_invalid() throws Exception {
        mockMvc.perform(post("/api/user/{username}/reserveAll", "ghost"))
//                .andExpect(status().isNotFound()); jwt - access denied
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelAllReservations_valid() throws Exception {
        mockMvc.perform(get("/api/user/cancelAllReservations/{username}", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void cancelAllReservations_invalid() throws Exception {
        mockMvc.perform(get("/api/user/cancelAllReservations/{username}", "ghost"))
//                .andExpect(status().isNotFound()); jwt - access denied
                .andExpect(status().isForbidden());
    }

    @Test
    void completeStayForAllBookings_allBookedByUser() throws Exception {
        Accommodation a1 = new Accommodation("HotelC1", AccommodationCategory.HOTEL, host, 2);
        Accommodation a2 = new Accommodation("HotelC2", AccommodationCategory.HOTEL, host, 2);
        a1 = accommodationService.save(a1).get();
        a2 = accommodationService.save(a2).get();

        accommodationService.reserve(a1.getId(), user.getUsername());
        accommodationService.reserve(a2.getId(), user.getUsername());
        accommodationService.book(a1.getId(), user.getUsername());
        accommodationService.book(a2.getId(), user.getUsername());

        mockMvc.perform(get("/api/user/completeStayForAllBookings/{username}", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.bookings", hasSize(0)));
    }

    @Test
    void completeStayForAllBookings_partialUnavailable() throws Exception {
        User anotherUser = userService.register("CJ", "pass", "pass", "Carl", "Johnson", Role.ROLE_USER);

        Accommodation a1 = new Accommodation("HotelC3", AccommodationCategory.HOTEL, host, 2);
        Accommodation a2 = new Accommodation("HotelC4", AccommodationCategory.HOTEL, host, 2);
        a1 = accommodationService.save(a1).get();
        a2 = accommodationService.save(a2).get();

        accommodationService.reserve(a1.getId(), anotherUser.getUsername());
        accommodationService.book(a1.getId(), anotherUser.getUsername());

        accommodationService.reserve(a2.getId(), user.getUsername());
        accommodationService.book(a2.getId(), user.getUsername());

        mockMvc.perform(get("/api/user/completeStayForAllBookings/{username}", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.bookings", hasSize(0)));
    }

    @Test
    void completeStayForAllBookings_invalid() throws Exception {
        mockMvc.perform(get("/api/user/completeStayForAllBookings/{username}", "ghost"))
//                .andExpect(status().isNotFound()); jwt - access denied
                .andExpect(status().isForbidden());
    }

    @Test
    void findAllReservations_empty() throws Exception {
        mockMvc.perform(get("/api/user/{username}/reservations", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllBookings_empty() throws Exception {
        mockMvc.perform(get("/api/user/findAllBookings/{username}", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllUsers_nonEmpty() throws Exception {
        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"));
    }
}
