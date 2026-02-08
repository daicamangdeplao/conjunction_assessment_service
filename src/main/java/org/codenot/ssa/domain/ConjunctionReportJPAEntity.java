package org.codenot.ssa.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "conjunction_report")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConjunctionReportJPAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "object_a_id", nullable = false)
    private Long objectAId;

    @Column(name = "object_b_id", nullable = false)
    private Long objectBId;

    @Column(name = "report_text", nullable = false, columnDefinition = "text")
    private String reportText;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding", columnDefinition = "vector(1536)")
    private float[] embedding;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
