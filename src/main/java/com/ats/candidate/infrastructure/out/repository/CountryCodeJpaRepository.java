package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CountryCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryCodeJpaRepository extends JpaRepository<CountryCodeEntity, Long> {
    List<CountryCodeEntity> findByActiveTrueOrderByOrderNumberAscCountryNameAsc();
    boolean existsByIsoCodeIgnoreCaseAndActiveTrue(String isoCode);
}
