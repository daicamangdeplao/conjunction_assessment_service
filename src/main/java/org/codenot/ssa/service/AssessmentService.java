package org.codenot.ssa.service;

import org.codenot.ssa.controller.dto.ConjunctionAssessment;
import org.codenot.ssa.domain.AssessmentStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    // Due to the asynchronous processing, concurrent hash map is chosen for thread-safe
    private final Map<Long, ConjunctionAssessment> store = new ConcurrentHashMap<>();

    private final ConjunctionComputationService computationService;

    public AssessmentService(ConjunctionComputationService computationService) {
        this.computationService = computationService;
    }

    public Long submitAssessment(String primaryObj, String secondaryObj) {
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
                .thenAccept(probability -> {
                    store.put(id, ConjunctionAssessment.builder()
                            .id(id)
                            .primaryObjectId(primaryObj)
                            .secondaryObjectId(secondaryObj)
                            .status(AssessmentStatus.PENDING)
                            .collisionProbability(probability)
                            .build());
                })
                .exceptionally(exception -> {
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

    public ConjunctionAssessment getAssessment(Long id) {
        return store.get(id);
    }

    public List<Long> getAssessmentIds() {
        return new ArrayList<>(store.keySet());
    }
}
