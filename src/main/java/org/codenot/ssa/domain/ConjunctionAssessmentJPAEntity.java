package org.codenot.ssa.domain;

import jakarta.persistence.*;
import lombok.*;
import org.codenot.ssa.domain.constant.AssessmentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "conjunction_assessment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConjunctionAssessmentJPAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "primary_object_id", nullable = false)
    private Long primaryObjectId;

    @Column(name = "secondary_object_id", nullable = false)
    private Long secondaryObjectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssessmentStatus status;

    @Column(name = "collision_probability")
    private Double collisionProbability;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "priority_level", nullable = false)
    private Integer priorityLevel;

    @Column(name = "window_start", nullable = false)
    private LocalDateTime windowStart;

    @Column(name = "window_end", nullable = false)
    private LocalDateTime windowEnd;

    @Column(name = "time_step_minutes", nullable = false)
    private Integer timeStepMinutes;

    @PrePersist
    protected void onCreate() {
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
    }
}
