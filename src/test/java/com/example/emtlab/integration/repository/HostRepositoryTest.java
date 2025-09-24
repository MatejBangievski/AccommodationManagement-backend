package com.example.emtlab.integration.repository;

import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.projections.HostProjection;
import com.example.emtlab.model.views.HostsPerCountryView;
import com.example.emtlab.repository.CountryRepository;
import com.example.emtlab.repository.HostRepository;
import com.example.emtlab.repository.HostsPerCountryViewRepostiory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HostRepositoryTest extends AbstractRepositoryTest{

    @Autowired
    private HostRepository hostRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private HostsPerCountryViewRepostiory hostsPerCountryViewRepostiory;

    private Country country;

    @BeforeEach
    void setUp() {
        country = countryRepository.save(new Country("Macedonia", "Europe"));
    }

    @Test
    void saveShouldPersistHost() {
        Host host = new Host("Jane", "Doe", country);
        Host saved = hostRepository.save(host);

        assertThat(saved.getId()).isNotNull();
        Optional<Host> found = hostRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Jane");
    }

    @Test
    void findAllShouldReturnAllHosts() {
        hostRepository.save(new Host("Alice", "Smith", country));
        hostRepository.save(new Host("Bob", "Johnson", country));

        List<Host> hosts = hostRepository.findAll();
        assertThat(hosts).hasSize(2).extracting(Host::getName)
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void takeNameAndSurnameByProjectionShouldReturnCorrectFields() {
        hostRepository.save(new Host("Charlie", "Brown", country));

        List<HostProjection> projections = hostRepository.takeNameAndSurnameByProjection();

        assertThat(projections).isNotEmpty();
        assertThat(projections.get(0).getName()).isEqualTo("Charlie");
        assertThat(projections.get(0).getSurname()).isEqualTo("Brown");
    }

    @Disabled
    @Test
    void refreshMaterializedViewShouldRunWithoutError() {
        hostRepository.save(new Host("Diana", "Prince", country));

        hostsPerCountryViewRepostiory.refreshMaterializedView();

        List<HostsPerCountryView> view = hostsPerCountryViewRepostiory.findAll();
        assertThat(view).isNotNull();
        assertThat(view).anyMatch(v -> v.getCountryId().equals(country.getId()));
    }
}
