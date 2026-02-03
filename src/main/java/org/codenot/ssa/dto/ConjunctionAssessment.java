package org.codenot.ssa.dto;

import lombok.Builder;
import org.codenot.ssa.domain.ConjunctionAssessmentJPAEntity;
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
        LocalDateTime windowEnd
) {

    public static ConjunctionAssessment fromJPAEntity(ConjunctionAssessmentJPAEntity entity) {
        return ConjunctionAssessment.builder()
                .id(entity.getId())
                .primaryObjectId(entity.getPrimaryObjectId())
                .secondaryObjectId(entity.getSecondaryObjectId())
                .status(entity.getStatus())
                .collisionProbability(BigDecimal.valueOf(entity.getCollisionProbability()))
                .requestedAt(entity.getRequestedAt())
                .completedAt(entity.getCompletedAt())
                .priorityLevel(entity.getPriorityLevel())
                .windowStart(entity.getWindowStart())
                .windowEnd(entity.getWindowEnd())
                .build();
    }

    public static ConjunctionAssessment empty() {
        return ConjunctionAssessment.builder().build();
    }
}
