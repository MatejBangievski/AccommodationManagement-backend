package com.example.emtlab.integration.repository;

import com.example.emtlab.integration.repository.AbstractRepositoryTest;
import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.enumerations.AccommodationCategory;
import com.example.emtlab.model.projections.AccommodationProjection;
import com.example.emtlab.model.views.AccommodationsPerHostView;
import com.example.emtlab.repository.AccommodationRepository;
import com.example.emtlab.repository.AccommodationsPerHostViewRepository;
import com.example.emtlab.repository.CountryRepository;
import com.example.emtlab.repository.HostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class AccommodationRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private AccommodationsPerHostViewRepository accommodationsPerHostViewRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private HostRepository hostRepository;

    private Host host;

    @BeforeEach
    void setUp() {
        Country country = countryRepository.save(new Country("Macedonia", "Europe"));
        host = hostRepository.save(new Host("John", "Doe", country));

        accommodationRepository.save(new Accommodation("Hotel One", AccommodationCategory.HOTEL, host, 10));
    }

    @Test
    void saveShouldPersistAccommodation() {
        Accommodation accommodation = new Accommodation("Test Hotel", AccommodationCategory.HOTEL, host, 2);
        Accommodation saved = accommodationRepository.save(accommodation);

        assertThat(saved.getId()).isNotNull();
        assertThat(accommodationRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void findAllShouldReturnAllAccommodations() {
        Accommodation acc2 = new Accommodation("Motel One", AccommodationCategory.MOTEL, host, 1);
        accommodationRepository.save(acc2);

        List<Accommodation> all = accommodationRepository.findAll();
        assertThat(all).hasSize(2).extracting(Accommodation::getName)
                .containsExactlyInAnyOrder("Hotel One", "Motel One");
    }

    @Test
    void takeCategoryAndCountByProjectionShouldReturnCorrectCounts() {
        accommodationRepository.save(new Accommodation("Stay1", AccommodationCategory.HOTEL, host, 2));
        accommodationRepository.save(new Accommodation("Stay2", AccommodationCategory.MOTEL, host, 1));

        List<AccommodationProjection> projections = accommodationRepository.takeCategoryAndCountByProjection();

        Map<String, Integer> categoryCounts = projections.stream()
                .collect(Collectors.toMap(AccommodationProjection::getCategory, AccommodationProjection::getCount));

        assertThat(categoryCounts).containsEntry("HOTEL", 2);
        assertThat(categoryCounts).containsEntry("MOTEL", 1);
    }

    @Disabled("Skipping because materialized view does not exist in test container")
    @Test
    void refreshMaterializedViewShouldRunWithoutError() {
        accommodationRepository.save(new Accommodation("Hotel A", AccommodationCategory.HOTEL, host, 2));
        accommodationRepository.save(new Accommodation("Hostel B", AccommodationCategory.MOTEL, host, 1));

        accommodationsPerHostViewRepository.refreshMaterializedView();

        List<AccommodationsPerHostView> view = accommodationsPerHostViewRepository.findAll();
        assertThat(view).isNotNull();
    }
}

