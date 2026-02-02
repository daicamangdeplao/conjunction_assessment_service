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
import java.util.concurrent.ThreadLocalRandom;

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

    @Deprecated
    public Long submitAssessmentAsync(Long primaryObj, Long secondaryObj) {
        long id = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        store.put(
                id,
                ConjunctionAssessment.builder()
                        .id(id)
                        .primaryObjectId(primaryObj)
                        .secondaryObjectId(secondaryObj)
                        .status(AssessmentStatus.PENDING)
                        .collisionProbability(null)
                        .build()
        );

        computationService.computeCollisionProbability()
                .thenAccept(probability -> store.put(id, ConjunctionAssessment.builder()
                        .id(id)
                        .primaryObjectId(primaryObj)
                        .secondaryObjectId(secondaryObj)
                        .status(AssessmentStatus.PENDING)
                        .collisionProbability(probability)
                        .build()))
                .exceptionally(_ -> {
                    store.put(id, ConjunctionAssessment.builder()
                            .id(id)
                            .primaryObjectId(primaryObj)
                            .secondaryObjectId(secondaryObj)
                            .status(AssessmentStatus.FAILED)
                            .collisionProbability(null)
                            .build());
                    return null;
                });

        return id;
    }

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
