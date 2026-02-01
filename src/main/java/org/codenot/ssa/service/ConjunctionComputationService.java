package org.codenot.ssa.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

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

    @Async("assessmentExecutor")
    public CompletableFuture<BigDecimal> computeCollisionProbabilityGateway(
            LocalDateTime windowStart,
            LocalDateTime windowEnd,
            Integer timeStepInMinutes
    ) {
        final long epochStart = windowStart.toEpochSecond(ZoneOffset.UTC);
        final long epochEnd = windowEnd.toEpochSecond(ZoneOffset.UTC);
        final int steps = Math.toIntExact((epochEnd - epochStart) / timeStepInMinutes);

        BigDecimal finalProbability = IntStream.range(0, steps)
                .mapToObj(_ -> {
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextLong(4000, 10000));
                    } catch (InterruptedException _) {
                        // Only for toy
                    }

                    double probability = Math.abs(new Random().nextGaussian()) * 1e-4;
                    return BigDecimal.valueOf(probability);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::max);

        return CompletableFuture.completedFuture(finalProbability);
    }
}
