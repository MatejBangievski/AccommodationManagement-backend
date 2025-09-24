package com.example.emtlab.integration.controller;

import com.example.emtlab.model.domain.Country;
import com.example.emtlab.service.domain.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CountryControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryService countryService;

    private Country existingCountry;

    @BeforeEach
    void setUp() {
        countryService.deleteAll();
        existingCountry = countryService.save(new Country("Macedonia", "Europe")).get();
    }

    @Test
    void findAll_nonEmptyList() throws Exception {
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Macedonia"));
    }

    @Test
    void findAll_emptyList() throws Exception {
        countryService.deleteAll();

        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findById_existing() throws Exception {
        mockMvc.perform(get("/api/countries/{id}", existingCountry.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Macedonia"))
                .andExpect(jsonPath("$.continent").value("Europe"));
    }

    @Test
    void findById_nonExisting() throws Exception {
        mockMvc.perform(get("/api/countries/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_valid() throws Exception {
        String json = """
                {
                  "name": "Germany",
                  "continent": "Europe"
                }
                """;

        mockMvc.perform(post("/api/countries/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Germany"))
                .andExpect(jsonPath("$.continent").value("Europe"));
    }

    @Test
    void save_invalid_missingField() throws Exception {
        String json = """
                {
                  "continent": "Asia"
                }
                """;

        mockMvc.perform(post("/api/countries/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_existing() throws Exception {
        String json = """
                {
                  "name": "North Macedonia",
                  "continent": "Europe"
                }
                """;

        mockMvc.perform(put("/api/countries/edit/{id}", existingCountry.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("North Macedonia"));
    }

    @Test
    void update_nonExisting() throws Exception {
        String json = """
                {
                  "name": "Atlantis",
                  "continent": "Ocean"
                }
                """;

        mockMvc.perform(put("/api/countries/edit/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existing() throws Exception {
        mockMvc.perform(delete("/api/countries/delete/{id}", existingCountry.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_nonExisting() throws Exception {
        mockMvc.perform(delete("/api/countries/delete/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
