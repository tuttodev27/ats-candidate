package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CandidateParseResult;

public interface CandidateParseResultRepositoryPort {
    CandidateParseResult save(CandidateParseResult result);
}
