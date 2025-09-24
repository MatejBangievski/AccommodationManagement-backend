package com.example.emtlab.integration.repository;

import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.repository.CountryRepository;
import com.example.emtlab.repository.HostRepository;
import com.example.emtlab.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private HostRepository hostRepository;

    private User user;

    @BeforeEach
    void setUp() {
        Country country = countryRepository.save(new Country("Macedonia", "Europe"));
        hostRepository.save(new Host("John", "Doe", country));

        user = new User("testuser", "password123", "Test", "User");
        userRepository.save(user);
    }

    @Test
    void saveShouldPersistUser() {
        User newUser = new User("alice", "securepass", "Alice", "Smith");
        User saved = userRepository.save(newUser);

        assertThat(saved.getUsername()).isEqualTo("alice");
        assertThat(userRepository.findById("alice")).isPresent();
    }

    @Test
    void findByUsernameAndPasswordShouldReturnUser() {
        Optional<User> found = userRepository.findByUsernameAndPassword("testuser", "password123");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test");
    }

    @Test
    void findByUsernameShouldReturnUser() {
        Optional<User> found = userRepository.findByUsername("testuser");

        assertThat(found).isPresent();
        assertThat(found.get().getSurname()).isEqualTo("User");
    }

    @Test
    void findAllWithoutReservationsShouldReturnUsers() {
        List<User> users = userRepository.findAllWithoutReservations();

        assertThat(users).isNotEmpty();
        assertThat(users).extracting(User::getUsername).contains("testuser");
    }
}