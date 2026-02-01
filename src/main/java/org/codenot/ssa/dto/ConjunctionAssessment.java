package org.codenot.ssa.dto;

import lombok.Builder;
import org.codenot.ssa.domain.constant.AssessmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ConjunctionAssessment(
        Long id,
        Long primaryObjectId,
        Long secondaryObjectId,
        AssessmentStatus status,
        BigDecimal collisionProbability,
        LocalDateTime requestedAt,
        LocalDateTime completedAt,
        Integer priorityLevel,
        LocalDateTime windowStart,
        LocalDateTime windowEnd,
        Integer timeStepMinutes
) {
}
