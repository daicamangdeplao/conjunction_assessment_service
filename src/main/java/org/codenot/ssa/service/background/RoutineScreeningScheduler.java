package org.codenot.ssa.service.background;

import org.codenot.ssa.domain.constant.OperationalStatus;
import org.codenot.ssa.repository.SpaceObjectRepository;
import org.codenot.ssa.service.AssessmentService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    // e.g., @Profile("dev") -> run as sequential that makes debug easier!
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

        // Log should come here
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
        // Log should come here
    }

    private void submitRoutineScreeningAsync() {
    }

    private Map<Long, List<Long>> constructScreeningPairs() {
        // Create the pairs
        // The pairs should be cached. The cached is only updated when detecting the change in database
        List<Long> activeSpaceObjectIds = spaceObjectRepository.findAllSpaceObjectIdsByOperationalStatus(OperationalStatus.ACTIVE);
        List<Long> allSpaceObjectIds = spaceObjectRepository.findAllSpaceObjectIds();

        return activeSpaceObjectIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        activeIds -> allSpaceObjectIds.stream()
                                .filter(id -> !id.equals(activeIds))
                                .toList()));
    }
}
