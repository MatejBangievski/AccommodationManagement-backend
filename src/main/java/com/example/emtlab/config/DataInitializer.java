package com.example.emtlab.config;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.AccommodationCategory;
import com.example.emtlab.model.enumerations.Role;
import com.example.emtlab.repository.AccommodationRepository;
import com.example.emtlab.repository.CountryRepository;
import com.example.emtlab.repository.HostRepository;
import com.example.emtlab.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final AccommodationRepository accommodationRepository;
    private final CountryRepository countryRepository;
    private final HostRepository hostRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AccommodationRepository accommodationRepository, CountryRepository countryRepository, HostRepository hostRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.accommodationRepository = accommodationRepository;
        this.countryRepository = countryRepository;
        this.hostRepository = hostRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        userRepository.save(new User(
                "user",
                passwordEncoder.encode("user"),
                "User",
                "User",
                Role.ROLE_USER
        ));
        userRepository.save(new User(
                "host",
                passwordEncoder.encode("host"),
                "Host",
                "Host",
                Role.ROLE_HOST
        ));
        userRepository.save(new User(
                "admin",
                passwordEncoder.encode("admin"),
                "Admin",
                "Admin",
                Role.ROLE_ADMIN
        ));
    }
}
