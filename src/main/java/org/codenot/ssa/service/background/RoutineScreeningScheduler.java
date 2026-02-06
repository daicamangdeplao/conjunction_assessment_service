package org.codenot.ssa.service.background;

import lombok.extern.slf4j.Slf4j;
import org.codenot.ssa.config.RabbitMQConfig;
import org.codenot.ssa.domain.constant.OperationalStatus;
import org.codenot.ssa.dto.OrbitUpdateMessage;
import org.codenot.ssa.repository.SpaceObjectRepository;
import org.codenot.ssa.service.AssessmentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RoutineScreeningScheduler {

    private final int timeStepInMinute;
    private final SpaceObjectRepository spaceObjectRepository;
    private final AssessmentService assessmentService;

    public RoutineScreeningScheduler(int timeStepInMinute, SpaceObjectRepository spaceObjectRepository, AssessmentService assessmentService) {
        this.timeStepInMinute = timeStepInMinute;
        this.spaceObjectRepository = spaceObjectRepository;
        this.assessmentService = assessmentService;
    }

    // This critical task is environment-dependent, so that this should be profilerized!
    // e.g.,
    // @Profile("dev") -> run as sequential that makes debug easier!
    // @Profile("stage") -> run as parallel that simulates the production-like performance!
    // @Profile("prod") -> run as parallel as expected!
    // Furthermore, the fixRate is also needed to be parameterized that allows us to run the service whenever we want!
    @Scheduled(fixedRate = 5000)
    @Profile("!dev")
    public void submitRoutineScreening() {
        Map<Long, List<Long>> screeningPairs = constructScreeningPairs();

        // Log should come here

        screeningPairs.entrySet().parallelStream().forEach(entry -> {
            Long primary = entry.getKey();
            entry.getValue().parallelStream()
                    .forEach(secondary -> {
                        LocalDateTime now = LocalDateTime.now();
                        assessmentService.submitAssessmentAsync(
                                primary,
                                secondary,
                                5,
                                now,
                                now.plusDays(1),
                                timeStepInMinute
                        );
                    });
        });

        // Log should come here

    }

    public void submitRoutineScreeningSync() {
        Map<Long, List<Long>> screeningPairs = constructScreeningPairs();

        int totalAssessments = screeningPairs.values().stream().mapToInt(List::size).sum();
        log.info("Starting routine screening sync: {} primary objects, {} total assessments, timeStep: {} minutes, period: 1 day",
                screeningPairs.size(), totalAssessments, timeStepInMinute);

        screeningPairs.forEach((primary, relatedObjects) -> relatedObjects.forEach(secondary -> {
            LocalDateTime now = LocalDateTime.now();
            assessmentService.submitAssessmentSync(
                    primary,
                    secondary,
                    5,
                    now,
                    now.plusDays(1),
                    timeStepInMinute
            );
        }));

        log.info("Completed routine screening sync: {} assessments submitted successfully", totalAssessments);
    }

    @RabbitListener(queues = RabbitMQConfig.ORBIT_UPDATE_QUEUE)
    public void handleOrbitUpdate(OrbitUpdateMessage msg) {
        log.info("Received orbit update message: {}", msg);
        submitRoutineScreeningSync();
    }

    private void submitRoutineScreeningAsync() {
    }

    private Map<Long, List<Long>> constructScreeningPairs() {
        // Create the pairs
        // The pairs should be cached. The cached is only updated when detecting the change in database
        // Cache at the service level
        // Invalidate on:
        //      space object insert/delete
        //      operational status change
        List<Long> activeSpaceObjectIds = spaceObjectRepository.findAllSpaceObjectIdsByOperationalStatus(OperationalStatus.ACTIVE);
        List<Long> allSpaceObjectIds = spaceObjectRepository.findAllSpaceObjectIds();

        /**
         * fewer allocations
         * No stream pipeline overhead
         * predictable memory layout
         * better JIT optimization
         * lower GC pressure
         * */
        Map<Long, List<Long>> screeningPairs = new HashMap<>(activeSpaceObjectIds.size());
        for (Long activeSpaceObjectId : activeSpaceObjectIds) {
            List<Long> targets = new ArrayList<>(allSpaceObjectIds.size() - 1);
            for (Long id : allSpaceObjectIds) {
                if (!id.equals(activeSpaceObjectId)) {
                    targets.add(id);
                }
            }
            screeningPairs.put(activeSpaceObjectId, targets);
        }
        return screeningPairs;

        /**
         * 1. Creat new Stream pipeline
         * 2. Allocate lambda object
         * 3. Invoke virtual call per element, i.e., Predicate.test()
         * 4. Use an internal resizing strategy
         * 5. Create an unmodifiable list wrapper
         *
         * more object creation
         * more indirection
         * more branch misprediction
         * more pressure on coung-gen GC
         *
         * Stream pipeline is preferred when:
         *  - run rarely
         *  - batch size is small (< few thousand)
         *  - clarity is more important than throughput
         * */
//        return activeSpaceObjectIds.stream()
//                .collect(Collectors.toMap(
//                        Function.identity(),
//                        activeIds -> allSpaceObjectIds.stream()
//                                .filter(id -> !id.equals(activeIds))
//                                .toList()));
    }
}
