package com.example.emtlab.component.service.domain;

import com.example.emtlab.model.domain.Country;
import com.example.emtlab.repository.CountryRepository;
import com.example.emtlab.service.domain.impl.CountryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CountryDomainServiceImplTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryServiceImpl countryService;

    private Country country1;
    private Country country2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        country1 = new Country("Macedonia", "Europe");
        country1.setId(1L);

        country2 = new Country("Brazil", "South America");
        country2.setId(2L);
    }

    // ===========================
    // Basic CRUD tests
    // ===========================

    @Test
    void findAllShouldReturnAllCountries() {
        when(countryRepository.findAll()).thenReturn(Arrays.asList(country1, country2));

        List<Country> result = countryService.findAll();

        assertThat(result).hasSize(2).containsExactly(country1, country2);
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    void findByIdShouldReturnCountryIfExists() {
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country1));

        Optional<Country> result = countryService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(country1);
        verify(countryRepository, times(1)).findById(1L);
    }

    @Test
    void findByIdShouldReturnEmptyIfNotExists() {
        when(countryRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Country> result = countryService.findById(3L);

        assertThat(result).isEmpty();
        verify(countryRepository, times(1)).findById(3L);
    }

    @Test
    void saveShouldReturnSavedCountry() {
        when(countryRepository.save(any(Country.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Country> result = countryService.save(country1);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(country1);
        verify(countryRepository, times(1)).save(country1);
    }

    @Test
    void updateShouldModifyExistingCountry() {
        Country update = new Country("North Macedonia", "Europe");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(country1));
        when(countryRepository.save(any(Country.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Country> result = countryService.update(1L, update);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("North Macedonia");
        assertThat(result.get().getContinent()).isEqualTo("Europe");
        verify(countryRepository, times(1)).findById(1L);
        verify(countryRepository, times(1)).save(country1);
    }

    @Test
    void updateShouldModifyOnlyNonNullFields() {
        Country update = new Country(null, "Balkan");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(country1));
        when(countryRepository.save(any(Country.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Country> result = countryService.update(1L, update);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Macedonia");
        assertThat(result.get().getContinent()).isEqualTo("Balkan");
        verify(countryRepository, times(1)).findById(1L);
        verify(countryRepository, times(1)).save(country1);
    }

    @Test
    void updateShouldReturnEmptyIfCountryDoesNotExist() {
        Country update = new Country("X", "Y");
        when(countryRepository.findById(5L)).thenReturn(Optional.empty());

        Optional<Country> result = countryService.update(5L, update);

        assertThat(result).isEmpty();
        verify(countryRepository, times(1)).findById(5L);
        verify(countryRepository, never()).save(any());
    }

    @Test
    void deleteByIdShouldCallRepository() {
        doNothing().when(countryRepository).deleteById(1L);

        countryService.deleteById(1L);

        verify(countryRepository, times(1)).deleteById(1L);
    }
}
