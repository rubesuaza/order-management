package com.example.management.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Address Value Object")
class AddressTest {

    @Test
    void shouldCreateWithAllAttributes() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        assertThat(address.getStreet()).isEqualTo("123 Main St");
        assertThat(address.getCity()).isEqualTo("New York");
        assertThat(address.getZipCode()).isEqualTo("10001");
        assertThat(address.getCountry()).isEqualTo("USA");
    }

    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        Address a = new Address("123 Main St", "New York", "10001", "USA");
        Address b = new Address("123 Main St", "New York", "10001", "USA");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenFieldsDiffer() {
        Address a = new Address("123 Main St", "New York", "10001", "USA");
        Address b = new Address("456 Oak Ave", "Boston", "02101", "USA");
        assertThat(a).isNotEqualTo(b);
    }
}
