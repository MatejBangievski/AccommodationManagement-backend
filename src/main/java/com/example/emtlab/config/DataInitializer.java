package com.example.emtlab.config;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.enumerations.AccommodationCategory;
import com.example.emtlab.repository.AccommodationRepository;
import com.example.emtlab.repository.CountryRepository;
import com.example.emtlab.repository.HostRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final AccommodationRepository accommodationRepository;
    private final CountryRepository countryRepository;
    private final HostRepository hostRepository;

    public DataInitializer(AccommodationRepository accommodationRepository, CountryRepository countryRepository, HostRepository hostRepository) {
        this.accommodationRepository = accommodationRepository;
        this.countryRepository = countryRepository;
        this.hostRepository = hostRepository;
    }

    @PostConstruct
    public void init() {
        Country england = new Country("England", "Europe");
        Country belgium = new Country("Belgium", "Europe");
        Country macedonia = new Country("Macedonia", "Europe");

        countryRepository.save(england);
        countryRepository.save(belgium);
        countryRepository.save(macedonia);

        Host host1 = new Host("John", "Doe", england);
        Host host2 = new Host("Pierre", "Dupont", belgium);
        Host host3 = new Host("Marko", "Doeski", macedonia);

        hostRepository.save(host1);
        hostRepository.save(host2);
        hostRepository.save(host3);

        accommodationRepository.save(new Accommodation("Castle Oakwood", AccommodationCategory.HOTEL, host1, 12));
        accommodationRepository.save(new Accommodation("Pierre's", AccommodationCategory.APARTMENT, host2, 2));
        accommodationRepository.save(new Accommodation("Dom", AccommodationCategory.HOUSE, host1, 6));
    }
}
