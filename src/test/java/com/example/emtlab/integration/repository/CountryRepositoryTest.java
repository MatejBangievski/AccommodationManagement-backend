package com.example.emtlab.integration.repository;

import com.example.emtlab.model.domain.Country;
import com.example.emtlab.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CountryRepositoryTest extends AbstractRepositoryTest{

    @Autowired
    private CountryRepository countryRepository;

    private Country country;

    @BeforeEach
    void setUp() {
        country = new Country("Macedonia", "Europe");
        country = countryRepository.save(country);
    }

    @Test
    void saveShouldPersistCountry() {
        Country saved = countryRepository.save(new Country("France", "Europe"));

        assertThat(saved.getId()).isNotNull();
        assertThat(countryRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void findByIdShouldReturnCountry() {
        Optional<Country> found = countryRepository.findById(country.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Macedonia");
    }

    @Test
    void updateShouldChangeCountryFields() {
        country.setName("North Macedonia");
        countryRepository.save(country);

        Country updated = countryRepository.findById(country.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("North Macedonia");
    }

    @Test
    void deleteShouldRemoveCountry() {
        countryRepository.delete(country);

        assertThat(countryRepository.findById(country.getId())).isEmpty();
    }
}
