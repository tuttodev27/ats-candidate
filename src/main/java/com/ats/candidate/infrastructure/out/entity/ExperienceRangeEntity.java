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
@Table(name = "experience_range")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExperienceRangeEntity {
    @Id
    Long id;

    String label;

    @Column(name = "min_years")
    Integer minYears;

    @Column(name = "max_years")
    Integer maxYears;

    @Column(name = "order_number")
    Integer orderNumber;

    Boolean active;
}
