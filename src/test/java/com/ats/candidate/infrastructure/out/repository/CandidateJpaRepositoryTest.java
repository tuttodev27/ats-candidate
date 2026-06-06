package com.ats.candidate.infrastructure.out.repository;

import com.ats.candidate.infrastructure.out.entity.CandidateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CandidateJpaRepositoryTest {

    @Autowired
    private CandidateJpaRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        CandidateEntity activePerez = CandidateEntity.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@example.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        CandidateEntity inactiveGomez = CandidateEntity.builder()
                .firstName("Ana")
                .lastName("Gomez")
                .email("ana.gomez@example.com")
                .active(false)
                .createdAt(LocalDateTime.now())
                .build();

        CandidateEntity activeSmith = CandidateEntity.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@example.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(activePerez);
        repository.save(inactiveGomez);
        repository.save(activeSmith);
    }

    @Test
    void findByActiveAndSearchFiltersByActiveCorrectly() {
        Page<CandidateEntity> activeCandidates = repository.findByActiveAndSearch(true, null, PageRequest.of(0, 10));
        assertThat(activeCandidates.getContent()).hasSize(2);
        assertThat(activeCandidates.getContent()).allMatch(CandidateEntity::getActive);

        Page<CandidateEntity> inactiveCandidates = repository.findByActiveAndSearch(false, null, PageRequest.of(0, 10));
        assertThat(inactiveCandidates.getContent()).hasSize(1);
        assertThat(inactiveCandidates.getContent().get(0).getLastName()).isEqualTo("Gomez");
    }

    @Test
    void findByActiveAndSearchFiltersBySearchTextCorrectly() {
        // Search in firstName (case-insensitive substring)
        Page<CandidateEntity> searchJuan = repository.findByActiveAndSearch(null, "jua", PageRequest.of(0, 10));
        assertThat(searchJuan.getContent()).hasSize(1);
        assertThat(searchJuan.getContent().get(0).getFirstName()).isEqualTo("Juan");

        // Search in lastName (case-insensitive substring)
        Page<CandidateEntity> searchGomez = repository.findByActiveAndSearch(null, "Gom", PageRequest.of(0, 10));
        assertThat(searchGomez.getContent()).hasSize(1);
        assertThat(searchGomez.getContent().get(0).getLastName()).isEqualTo("Gomez");

        // Search in email (case-insensitive substring)
        Page<CandidateEntity> searchEmail = repository.findByActiveAndSearch(null, "smith", PageRequest.of(0, 10));
        assertThat(searchEmail.getContent()).hasSize(1);
        assertThat(searchEmail.getContent().get(0).getLastName()).isEqualTo("Smith");

        // Search with no matches
        Page<CandidateEntity> searchNoMatch = repository.findByActiveAndSearch(null, "unknown", PageRequest.of(0, 10));
        assertThat(searchNoMatch.getContent()).isEmpty();
    }

    @Test
    void findByActiveAndSearchWithBothFiltersWorks() {
        Page<CandidateEntity> searchActivePerez = repository.findByActiveAndSearch(true, "perez", PageRequest.of(0, 10));
        assertThat(searchActivePerez.getContent()).hasSize(1);
        assertThat(searchActivePerez.getContent().get(0).getLastName()).isEqualTo("Perez");

        // If inactive "perez" is searched with active=true, it shouldn't match
        Page<CandidateEntity> searchInactivePerez = repository.findByActiveAndSearch(false, "perez", PageRequest.of(0, 10));
        assertThat(searchInactivePerez.getContent()).isEmpty();
    }
}
