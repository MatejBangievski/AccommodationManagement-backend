package com.example.emtlab.component.entity;

import com.example.emtlab.model.domain.Country;
import com.example.emtlab.model.domain.Host;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HostEntityTest {

    @Test
    void constructorShouldInitializeFields() {
        Country country = new Country("Macedonia", "Europe");
        Host host = new Host("Nikola", "Stojanovski", country);

        assertThat(host.getName()).isEqualTo("Nikola");
        assertThat(host.getSurname()).isEqualTo("Stojanovski");
        assertThat(host.getCountry()).isEqualTo(country);
    }
}
