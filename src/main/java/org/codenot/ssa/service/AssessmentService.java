package org.codenot.ssa.service;

import org.codenot.ssa.domain.ConjunctionAssessmentJPAEntity;
import org.codenot.ssa.domain.constant.AssessmentStatus;
import org.codenot.ssa.dto.ConjunctionAssessment;
import org.codenot.ssa.repository.ConjunctionAssessmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AssessmentService {

    // Due to the asynchronous processing, concurrent hash map is chosen for thread-safe
    private final Map<Long, ConjunctionAssessment> store = new ConcurrentHashMap<>();
    private final ConjunctionAssessmentRepository conjunctionAssessmentRepository;

    private final ConjunctionComputationService computationService;

    public AssessmentService(ConjunctionAssessmentRepository conjunctionAssessmentRepository, ConjunctionComputationService computationService) {
        this.conjunctionAssessmentRepository = conjunctionAssessmentRepository;
        this.computationService = computationService;
    }

    /**
     * Evaluates the conjunction risk between a primary and a secondary space object.
     *
     * <p>The <strong>primary object</strong> represents the protected asset for which
     * risk is assessed and potential mitigation actions are planned. It is typically
     * an active, maneuverable spacecraft operated by the organization or its customer.</p>
     *
     * <p>The <strong>secondary object</strong> represents the encounter object and is
     * included solely for risk assessment. It is often non-maneuverable or externally
     * owned, and no action is expected from it.</p>
     *
     * <h3>Examples</h3>
     * <ul>
     *   <li>Primary object: Earth-observation satellite, GNSS satellite, crewed vehicle</li>
     *   <li>Secondary object: debris fragment, defunct satellite, rocket body</li>
     * </ul>
     *
     * @param primary   the protected asset for which conjunction risk is computed and
     *                  avoidance actions may be planned
     * @param secondary the encounter object included only to assess collision risk;
     *                  no maneuver is assumed
     * @return a {@code ConjunctionRiskResult} containing probability and miss-distance metrics
     * @throws IllegalArgumentException if either object is {@code null}
     * @since 1.0
     */
    @Transactional
    public void submitAssessmentAsync(
            Long primaryObj,
            Long secondaryObj,
            Integer priority,
            LocalDateTime windowStart,
            LocalDateTime windowEnd,
            Integer timeStepInMinutes
    ) {
        ConjunctionAssessmentJPAEntity conjunctionAssessment = ConjunctionAssessmentJPAEntity.builder()
                .primaryObjectId(primaryObj)
                .secondaryObjectId(secondaryObj)
                .status(AssessmentStatus.PENDING)
                .priorityLevel(priority)
                .requestedAt(LocalDateTime.now())
                .windowStart(windowStart)
                .windowEnd(windowEnd)
                .timeStepMinutes(timeStepInMinutes)
                .build();
        conjunctionAssessmentRepository.save(conjunctionAssessment);
        computationService.computeCollisionProbabilityGatewayAsync(windowStart, windowEnd, timeStepInMinutes)
                .thenAccept(probability -> {
                    conjunctionAssessment.setStatus(AssessmentStatus.COMPLETED);
                    conjunctionAssessment.setCollisionProbability(probability.doubleValue());
                    conjunctionAssessment.setCompletedAt(LocalDateTime.now());
                    conjunctionAssessmentRepository.save(conjunctionAssessment);
                })
                .exceptionally(_ -> {
                    conjunctionAssessment.setStatus(AssessmentStatus.FAILED);
                    conjunctionAssessment.setCompletedAt(LocalDateTime.now());
                    conjunctionAssessmentRepository.save(conjunctionAssessment);
                    return null;
                });
    }

    @Transactional
    public void submitAssessmentSync(
            Long primaryObj,
            Long secondaryObj,
            Integer priority,
            LocalDateTime windowStart,
            LocalDateTime windowEnd,
            Integer timeStepInMinutes
    ) {
        ConjunctionAssessmentJPAEntity conjunctionAssessment = ConjunctionAssessmentJPAEntity.builder()
                .primaryObjectId(primaryObj)
                .secondaryObjectId(secondaryObj)
                .status(AssessmentStatus.PENDING)
                .priorityLevel(priority)
                .requestedAt(LocalDateTime.now())
                .windowStart(windowStart)
                .windowEnd(windowEnd)
                .timeStepMinutes(timeStepInMinutes)
                .build();
        conjunctionAssessmentRepository.save(conjunctionAssessment);
        BigDecimal probability = computationService.computeCollisionProbabilityGatewaySync(windowStart, windowEnd, timeStepInMinutes);
        conjunctionAssessment.setStatus(AssessmentStatus.COMPLETED);
        conjunctionAssessment.setCollisionProbability(probability.doubleValue());
        conjunctionAssessment.setCompletedAt(LocalDateTime.now());
        conjunctionAssessmentRepository.save(conjunctionAssessment);
    }

    public ConjunctionAssessment getAssessment(Long id) {
        return store.get(id);
    }

    public List<Long> getAssessmentIds() {
        return new ArrayList<>(store.keySet());
    }
}
