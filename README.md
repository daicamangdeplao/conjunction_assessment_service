# Asynchronous Conjunction Assessment Service

## Domain framing (Space Situational Awareness (SSA), Space Traffic Management (STM) aligned)

In real SSA systems, conjunction assessments (collision risk checks between space objects) are:

* computationally expensive,
* triggered frequently,
* never done synchronously in request/response paths.

## Core idea

A REST API receives a conjunction assessment request for two space objects.

* The request is acknowledged immediately.
* The collision probability computation runs asynchronously.
* Results are stored and can be queried later.

## Async techniques demonstrated

* Fire-and-forget job submission
* @Async with bounded executors
* CompletableFuture for result tracking
* Polling-based status retrieval (very common in STM systems)
* Clear separation between operational and analytical paths

## Minimal functional requirements

* POST /assessments
  * Accepts two object IDs + time window
  * Returns an assessmentId immediately
* Async background task
  * Simulates orbit propagation + miss-distance computation
  * Takes several seconds
* GET /assessments/{id}
  * Returns status: PENDING | RUNNING | COMPLETED | FAILED
  * Includes collision probability when done

## High-level architecture

Controller
↓
AssessmentService (sync)
↓
AssessmentExecutor (@Async)
↓
In-memory store (ConcurrentHashMap)

## Domain model (simplified)

````java
public enum AssessmentStatus {
    PENDING, RUNNING, COMPLETED, FAILED
}

public record ConjunctionAssessment(
        UUID id,
        String primaryObjectId,
        String secondaryObjectId,
        AssessmentStatus status,
        Double collisionProbability
) {}

````

## Async executor configuration

````java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "assessmentExecutor")
    public Executor assessmentExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(2);
        exec.setMaxPoolSize(4);
        exec.setQueueCapacity(50);
        exec.setThreadNamePrefix("ssa-ca-");
        exec.initialize();
        return exec;
    }
}

````

## Async computation service (SSA flavor)

````java
@Service
public class ConjunctionComputationService {

    @Async("assessmentExecutor")
    public CompletableFuture<Double> computeCollisionProbability() {

        try {
            // simulate orbit propagation & covariance analysis
            Thread.sleep(4000);

            // fake but realistic-looking probability
            double probability = Math.random() * 1e-4;

            return CompletableFuture.completedFuture(probability);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }
}
````

## Orchestration service

````java
@Service
public class AssessmentService {

    private final ConjunctionComputationService computationService;
    private final Map<UUID, ConjunctionAssessment> store = new ConcurrentHashMap<>();

    public AssessmentService(ConjunctionComputationService computationService) {
        this.computationService = computationService;
    }

    public UUID submitAssessment(String objA, String objB) {
        UUID id = UUID.randomUUID();

        store.put(id, new ConjunctionAssessment(
                id, objA, objB, AssessmentStatus.PENDING, null));

        computationService.computeCollisionProbability()
            .thenAccept(prob -> store.put(id,
                new ConjunctionAssessment(id, objA, objB,
                        AssessmentStatus.COMPLETED, prob)))
            .exceptionally(ex -> {
                store.put(id,
                    new ConjunctionAssessment(id, objA, objB,
                            AssessmentStatus.FAILED, null));
                return null;
            });

        return id;
    }

    public ConjunctionAssessment getAssessment(UUID id) {
        return store.get(id);
    }
}
````

## REST controller

````java
@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    public ResponseEntity<UUID> submit(
            @RequestParam String primary,
            @RequestParam String secondary) {

        UUID id = assessmentService.submitAssessment(primary, secondary);
        return ResponseEntity.accepted().body(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConjunctionAssessment> get(@PathVariable UUID id) {
        return ResponseEntity.ok(assessmentService.getAssessment(id));
    }
}
````

# Why this fits SSA / STM / FD / STC well

* Asynchronous by nature (no blocking during propagation)
* Mirrors real conjunction screening workflows
* Conservative polling model (common in ops systems)
* Clean separation of command vs query
* Easy to evolve toward:
  * maneuver planning (FD),
  * priority queues (STC),
  * message-driven pipelines (STM)

# Natural extensions (realistic next steps)

* Time-window parameterization
* Priority levels (human-in-the-loop STC)
* Rate limiting per operator
* Replace in-memory store with PostgreSQL
* Event emission on high-risk conjunctions
