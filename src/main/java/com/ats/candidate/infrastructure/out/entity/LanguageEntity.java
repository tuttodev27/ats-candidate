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
@Table(name = "language")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LanguageEntity {
    @Id
    Long id;

    String name;

    @Column(name = "iso_code")
    String isoCode;

    Boolean active;
}
