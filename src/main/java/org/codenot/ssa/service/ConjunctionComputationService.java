package org.codenot.ssa.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Service
public class ConjunctionComputationService {

    @Async("assessmentExecutor")
    public CompletableFuture<BigDecimal> computeCollisionProbabilityGatewayAsync(
            final LocalDateTime windowStart,
            final LocalDateTime windowEnd,
            final Integer timeStepInMinutes
    ) {
        final int steps = calculatePropagationSteps(windowStart, windowEnd, timeStepInMinutes);
        BigDecimal finalProbability = simulateProbabilityCalculation(steps);
        return CompletableFuture.completedFuture(finalProbability);
    }

    public BigDecimal computeCollisionProbabilityGatewaySync(
            final LocalDateTime windowStart,
            final LocalDateTime windowEnd,
            final Integer timeStepInMinutes
    ) {
        final int steps = calculatePropagationSteps(windowStart, windowEnd, timeStepInMinutes);
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

    private int calculatePropagationSteps(LocalDateTime windowStart, LocalDateTime windowEnd, Integer timeStepInMinutes) {
        final Duration duration = Duration.between(windowStart, windowEnd);
        final long durationInMinute = duration.toMinutes();
        return Math.toIntExact(durationInMinute / timeStepInMinutes);
    }
}
