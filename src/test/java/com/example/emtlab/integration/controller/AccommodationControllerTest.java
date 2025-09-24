package com.example.emtlab.integration.controller;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.AccommodationCategory;
import com.example.emtlab.model.enumerations.Role;
import com.example.emtlab.service.domain.AccommodationService;
import com.example.emtlab.service.domain.CountryService;
import com.example.emtlab.service.domain.HostService;
import com.example.emtlab.service.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccommodationControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccommodationService accommodationService;

    @Autowired
    private HostService hostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CountryService countryService;

    private Host host;
    private User user;
    private Country country;

    @BeforeEach
    void setUp() {
        country = countryService.save(new Country("Test country", "Test continent")).get();
        host = hostService.save(new Host("TesterHost", "T", country)).get();

        String username = "TestUser-" + UUID.randomUUID(); //Random user everytime
        user = userService.register(username, "testPassword", "testPassword", "TesterName", "TesterSurname", Role.ROLE_ADMIN);
    }

    @Test
    void createAccommodation_valid() throws Exception {
        String json = """
            {
              "name": "Hotel Plaza",
              "category": "HOTEL",
              "hostId": %d,
              "numRooms": 5
            }
            """.formatted(host.getId());

        String responseBody = mockMvc.perform(post("/api/accommodations/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Hotel Plaza"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJson = mapper.readTree(responseBody);
        Long createdId = responseJson.get("id").asLong();

        accommodationService.findById(createdId).orElseThrow();
    }

    @Test
    void createAccommodation_missingRooms() throws Exception {
        String json = """
                {
                  "name": "No Rooms Hotel",
                  "category": "HOTEL",
                  "hostId": %d
                }
                """.formatted(host.getId());

        mockMvc.perform(post("/api/accommodations/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllAccommodations_nonEmpty() throws Exception {
        Accommodation a = accommodationService.save(new Accommodation("Small house", AccommodationCategory.HOUSE, host, 4)).get();

        mockMvc.perform(get("/api/accommodations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAccommodationById_existing() throws Exception {
        Accommodation a = accommodationService
                .save(new Accommodation("Test apartment", AccommodationCategory.APARTMENT, host, 3)).get();

        mockMvc.perform(get("/api/accommodations/{id}", a.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test apartment"));
    }

    @Test
    void getAccommodationById_nonExisting() throws Exception {
        mockMvc.perform(get("/api/accommodations/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    /*
        Second parameter is Authentication.
        From there we extract the user.
     */
    @Test
    void reserveAccommodation_valid() throws Exception {
        Accommodation a = accommodationService
                .save(new Accommodation("ResHotel", AccommodationCategory.HOTEL, host, 2)).get();

        mockMvc.perform(post("/api/accommodations/{id}/reserve", a.getId())
                        .with(user(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isReserved").value(true))
                .andExpect(jsonPath("$.isBooked").value(false));
    }

    @Test
    void reserveAccommodation_alreadyReserved() throws Exception {
        Accommodation a = new Accommodation("ResHotel", AccommodationCategory.HOTEL, host, 2);
        a.setReserved(true);
        a = accommodationService.save(a).get();

        mockMvc.perform(post("/api/accommodations/{id}/reserve", a.getId())
                        .with(user(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookAccommodation_valid() throws Exception {
        Accommodation a = new Accommodation("BookHotel", AccommodationCategory.HOTEL, host, 4);
        a = accommodationService.save(a).get();
        a = accommodationService.reserve(a.getId(), user.getUsername()).get();

        mockMvc.perform(post("/api/accommodations/{id}/book", a.getId())
                        .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isBooked").value(true));
    }

    @Test
    void completeStay_valid() throws Exception {
        Accommodation a = new Accommodation("CompleteHotel", AccommodationCategory.HOTEL, host, 2);
        a = accommodationService.save(a).get();
        a = accommodationService.reserve(a.getId(), user.getUsername()).get();

        User bookedUser = userService.findByUsername(user.getUsername());
        a = accommodationService.book(a.getId(), bookedUser.getUsername()).get();

        mockMvc.perform(post("/api/accommodations/{id}/completeStay", a.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isBooked").value(false))
                .andExpect(jsonPath("$.isReserved").value(false));
    }

    @Test
    void completeStay_invalidNotBooked() throws Exception {
        Accommodation a = new Accommodation("NotBookedHotel", AccommodationCategory.HOTEL, host, 2);
        a = accommodationService.save(a).get();

        mockMvc.perform(post("/api/accommodations/{id}/completeStay", a.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAccommodation_existing() throws Exception {
        Accommodation a = new Accommodation("DeleteHotel", AccommodationCategory.HOTEL, host, 6);
        a = accommodationService.save(a).get();

        mockMvc.perform(delete("/api/accommodations/delete/{id}", a.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAccommodation_nonExisting() throws Exception {
        mockMvc.perform(delete("/api/accommodations/delete/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findStatistics_returnsCounts() throws Exception {
        Accommodation a1 = new Accommodation("Hotel1", AccommodationCategory.HOTEL, host, 2);
        Accommodation a2 = new Accommodation("Motel1", AccommodationCategory.MOTEL, host, 1);
        a1 = accommodationService.save(a1).get();
        a2 = accommodationService.save(a2).get();

        mockMvc.perform(get("/api/accommodations/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.category=='HOTEL')].category").exists())
                .andExpect(jsonPath("$[?(@.category=='HOTEL')].count").exists())
                .andExpect(jsonPath("$[?(@.category=='MOTEL')].category").exists())
                .andExpect(jsonPath("$[?(@.category=='MOTEL')].count").exists());
    }

    @Test
    void fetchAccommodation_existing() throws Exception {
        Accommodation a = new Accommodation("DetailHotel", AccommodationCategory.HOTEL, host, 2);
        a = accommodationService.save(a).get();

        mockMvc.perform(get("/api/accommodations/{id}/details", a.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("DetailHotel"));
    }

    @Test
    void fetchAccommodation_nonExisting() throws RuntimeException {
        try {
            mockMvc.perform(get("/api/accommodations/{id}/details", 999L))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
