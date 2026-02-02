package org.codenot.ssa.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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
    public CompletableFuture<BigDecimal> computeCollisionProbabilityGatewayAsync(
            final LocalDateTime windowStart,
            final LocalDateTime windowEnd,
            final Integer timeStepInMinutes
    ) {
        final long epochStart = windowStart.toEpochSecond(ZoneOffset.UTC);
        final long epochEnd = windowEnd.toEpochSecond(ZoneOffset.UTC);
        final int steps = Math.toIntExact((epochEnd - epochStart) / timeStepInMinutes);

        BigDecimal finalProbability = simulateProbabilityCalculation(steps);

        return CompletableFuture.completedFuture(finalProbability);
    }

    public BigDecimal computeCollisionProbabilityGatewaySync(
            final LocalDateTime windowStart,
            final LocalDateTime windowEnd,
            final Integer timeStepInMinutes
    ) {
        final Duration duration = Duration.between(windowStart, windowEnd);
        final long durationInMinute = duration.toMinutes();
        final int steps = Math.toIntExact(durationInMinute / timeStepInMinutes);
        return simulateProbabilityCalculation(steps);
    }

    private BigDecimal simulateProbabilityCalculation(int steps) {
        return IntStream.range(0, steps)
                .mapToObj(_ -> {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException _) {
                        // Only for toy
                    }

                    double probability = Math.abs(new Random().nextGaussian()) * 1e-4;
                    return BigDecimal.valueOf(probability);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::max);
    }
}
