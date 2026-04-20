package com.ats.candidate.domain.port.in.usecase;

import com.ats.candidate.domain.model.Candidate;

public interface CandidateUseCase {
    Candidate create(Candidate candidate, Long recruiterId);

}
