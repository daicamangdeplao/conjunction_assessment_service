package org.codenot.ssa.domain;

import jakarta.persistence.*;
import lombok.*;
import org.codenot.ssa.domain.constant.EphemerisSource;
import org.codenot.ssa.domain.constant.ObjectType;
import org.codenot.ssa.domain.constant.OperationalStatus;
import org.codenot.ssa.domain.constant.OrbitRegime;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "space_object")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceObjectJPAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "norad_catalog_number", unique = true, nullable = false)
    private Integer noradCatalogNumber;

    @Column(name = "object_name", length = 128, nullable = false)
    private String objectName;

    @Enumerated(EnumType.STRING)
    @Column(name = "object_type", length = 32, nullable = false)
    private ObjectType objectType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operational_status", length = 32, nullable = false)
    private OperationalStatus operationalStatus;

    @Column(name = "owner_operator", length = 64)
    private String ownerOperator;

    @Column(name = "launch_date")
    private LocalDate launchDate;

    @Column(name = "mass_kg")
    private Double massKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "orbit_regime", length = 16, nullable = false)
    private OrbitRegime orbitRegime;

    @Enumerated(EnumType.STRING)
    @Column(name = "ephemeris_source", length = 32, nullable = false)
    private EphemerisSource ephemerisSource;

    @Column(name = "last_ephemeris_update", nullable = false)
    private LocalDateTime lastEphemerisUpdate;

    @Column(name = "ephemeris_accuracy_m")
    private Double ephemerisAccuracyM;

    @Column(name = "mean_altitude_km")
    private Double meanAltitudeKm;

    @Column(name = "inclination_deg")
    private Double inclinationDeg;

    @Column(name = "eccentricity")
    private Double eccentricity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
