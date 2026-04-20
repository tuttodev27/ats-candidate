package com.ats.candidate.application.service;

import com.ats.candidate.domain.exception.EmailAlreadyExistException;
import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.port.in.usecase.CandidateUseCase;
import com.ats.candidate.domain.port.out.repository.CandidateRepositoryPort;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class CandidateService implements CandidateUseCase {

    private final CandidateRepositoryPort candidateRepositoryPort;

    public CandidateService(CandidateRepositoryPort candidateRepositoryPort) {
        this.candidateRepositoryPort = candidateRepositoryPort;
    }

    @Override
    public Candidate create(Candidate candidate, Long recruiterId) {
        if (candidateRepositoryPort.existsByEmail(candidate.getEmail())) {
            throw new EmailAlreadyExistException(candidate.getEmail());
        }
        candidate.setActive(true);
        candidate.setCreatedBy(recruiterId);
        candidate.setCreatedAt(LocalDateTime.now());

        return candidateRepositoryPort.save(candidate);
    }

    @Override
    public Page<Candidate> list(Boolean active, Pageable pageable) {
        return candidateRepositoryPort.findAll(active, pageable);
    }

}
