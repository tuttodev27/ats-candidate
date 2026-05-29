package com.ats.candidate.domain.model;

import lombok.Getter;

@Getter
public enum CandidateStatus {
    NEW("Nuevo"),
    IN_REVIEW("En revisión"),
    INTERVIEW("Entrevista"),
    SHORTLIST("Finalista"),
    REJECTED("Rechazado"),
    HIRED("Contratado");

    private final String label;

    CandidateStatus(String label) {
        this.label = label;
    }
}
