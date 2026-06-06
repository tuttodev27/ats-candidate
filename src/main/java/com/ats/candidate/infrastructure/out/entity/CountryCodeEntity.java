package com.ats.candidate.infrastructure.out.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "country_code")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CountryCodeEntity {
    @Id
    Long id;

    @Column(name = "country_name")
    String countryName;

    @Column(name = "iso_code")
    String isoCode;

    @Column(name = "phone_code")
    String phoneCode;

    @Column(name = "order_number")
    Integer orderNumber;

    Boolean active;
}
