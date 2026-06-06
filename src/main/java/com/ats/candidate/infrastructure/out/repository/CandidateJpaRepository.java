package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CandidateJpaRepository extends JpaRepository<CandidateEntity, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Page<CandidateEntity> findByActive(Boolean active, Pageable pageable);

    @Query("SELECT c FROM CandidateEntity c WHERE " +
           "(:active IS NULL OR c.active = :active) AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CandidateEntity> findByActiveAndSearch(
            @Param("active") Boolean active,
            @Param("search") String search,
            Pageable pageable);
}
