package org.codenot.ssa.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ConjunctionComputationService {

    @Async("assessmentExecutor")
    public CompletableFuture<BigDecimal> computeCollisionProbability() {
        try {
            // simulate orbit propagation & covariance analysis
            Thread.sleep(ThreadLocalRandom.current().nextLong(4000, 10000));

            double probability = Math.random() * 1e-4;

            return CompletableFuture.completedFuture(BigDecimal.valueOf(probability));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
