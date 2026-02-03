package org.codenot.ssa.repository;

import org.codenot.ssa.domain.ConjunctionAssessmentJPAEntity;
import org.codenot.ssa.domain.constant.AssessmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConjunctionAssessmentRepository extends JpaRepository<ConjunctionAssessmentJPAEntity, Long> {

    List<ConjunctionAssessmentJPAEntity> findByStatus(AssessmentStatus status);

    List<ConjunctionAssessmentJPAEntity> findByPrimaryObjectId(Long primaryObjectId);
    List<ConjunctionAssessmentJPAEntity> findByPrimaryObjectIdAndSecondaryObjectId(Long primaryObjectId, Long secondaryObjectId);

    List<ConjunctionAssessmentJPAEntity> findByPriorityLevelGreaterThanEqual(Integer priorityLevel);

    List<ConjunctionAssessmentJPAEntity> findByWindowStartBetween(LocalDateTime start, LocalDateTime end);

    List<ConjunctionAssessmentJPAEntity> findByStatusAndPriorityLevelGreaterThanEqual(
            AssessmentStatus status,
            Integer priorityLevel
    );
}
