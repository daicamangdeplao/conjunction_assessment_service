package org.codenot.ssa.controller.dto;

import lombok.Builder;
import org.codenot.ssa.domain.AssessmentStatus;

import java.math.BigDecimal;

@Builder
public record ConjunctionAssessment(
        Long id,
        String primaryObjectId,
        String secondaryObjectId,
        AssessmentStatus status,
        BigDecimal collisionProbability
) {
}
