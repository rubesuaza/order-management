package com.example.management.domain.model;

import java.util.Objects;

/**
 * Value object representing a physical address.
 * Immutable.
 */
public final class Address {

    private final String street;
    private final String city;
    private final String zipCode;
    private final String country;

    public Address(String street, String city, String zipCode, String country) {
        this.street = Objects.requireNonNull(street, "street must not be null");
        this.city = Objects.requireNonNull(city, "city must not be null");
        this.zipCode = Objects.requireNonNull(zipCode, "zipCode must not be null");
        this.country = Objects.requireNonNull(country, "country must not be null");
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street)
                && Objects.equals(city, address.city)
                && Objects.equals(zipCode, address.zipCode)
                && Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, zipCode, country);
    }
}
