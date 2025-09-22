package com.example.emtlab.component.entity;

import com.example.emtlab.model.domain.Country;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CountryEntityTest {

    @Test
    void constructorShouldInitializeFields() {
        Country country = new Country("Macedonia", "Europe");

        assertThat(country.getName()).isEqualTo("Macedonia");
        assertThat(country.getContinent()).isEqualTo("Europe");
    }
}
