package com.example.emtlab.integration.controller;

import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.service.domain.CountryService;
import com.example.emtlab.service.domain.HostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HostControllerTest extends AbstractControllerTest {

    @Autowired
    private HostService hostService;

    @Autowired
    private CountryService countryService;

    private Country country;

    @BeforeEach
    void setUpHost() {
        hostService.deleteAll();
        countryService.deleteAll();
        country = countryService.save(new Country("Testland", "Europe")).get();
    }

    @Test
    void findAll_empty() throws Exception {
        mockMvc.perform(get("/api/hosts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAll_nonEmpty() throws Exception {
        hostService.save(new Host("Alice", "Smith", country));

        mockMvc.perform(get("/api/hosts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }


    @Test
    void findById_existing() throws Exception {
        Host h = hostService.save(new Host("Bob", "Jones", country)).get();

        mockMvc.perform(get("/api/hosts/{id}", h.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"));
    }

    @Test
    void findById_nonExisting() throws Exception {
        mockMvc.perform(get("/api/hosts/{id}", 999L))
                .andExpect(status().isNotFound());
    }


    @Test
    void save_valid() throws Exception {
        String json = """
            { "name": "Charlie", "surname": "Brown", "countryId": %d }
        """.formatted(country.getId());

        mockMvc.perform(post("/api/hosts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Charlie"))
                .andExpect(jsonPath("$.surname").value("Brown"));
    }

    @Test
    void save_invalid_missingCountry() throws Exception {
        String json = """
            { "name": "Dave", "surname": "White", "countryId": 999 }
        """;

        mockMvc.perform(post("/api/hosts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }


    @Test
    void update_existing() throws Exception {
        Host h = hostService.save(new Host("Eve", "Taylor", country)).get();

        String json = """
            { "name": "Eva", "surname": "Taylor", "countryId": %d }
        """.formatted(country.getId());

        mockMvc.perform(put("/api/hosts/edit/{id}", h.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Eva"));
    }

    @Test
    void update_nonExisting() throws Exception {
        String json = """
            { "name": "Frank", "surname": "Adams", "countryId": %d }
        """.formatted(country.getId());

        mockMvc.perform(put("/api/hosts/edit/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }


    @Test
    void delete_existing() throws Exception {
        Host h = hostService.save(new Host("Grace", "Hall", country)).get();

        mockMvc.perform(delete("/api/hosts/delete/{id}", h.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void delete_nonExisting() throws Exception {
        mockMvc.perform(delete("/api/hosts/delete/{id}", 999L))
                .andExpect(status().isNotFound());
    }


    @Test
    void findHostsPerCountry_empty() throws Exception {
        mockMvc.perform(get("/api/hosts/by-country"))
                .andExpect(status().isOk());
    }

    @Test
    void getHostsByNameAndSurname_empty() throws Exception {
        mockMvc.perform(get("/api/hosts/names"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
