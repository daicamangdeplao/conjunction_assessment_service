package org.codenot.ssa.controller;

import org.codenot.ssa.dto.ConjunctionAssessment;
import org.codenot.ssa.service.AssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
public class AssessmentController {
    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    /**
     * Evaluates the conjunction risk between a primary and a secondary space object.
     *
     * <p>The <strong>primary object</strong> represents the protected asset for which
     * risk is assessed and potential mitigation actions are planned. It is typically
     * an active, maneuverable spacecraft operated by the organization or its customer.</p>
     *
     * <p>The <strong>secondary object</strong> represents the encounter object and is
     * included solely for risk assessment. It is often non-maneuverable or externally
     * owned, and no action is expected from it.</p>
     *
     * <h3>Examples</h3>
     * <ul>
     *   <li>Primary object: Earth-observation satellite, GNSS satellite, crewed vehicle</li>
     *   <li>Secondary object: debris fragment, defunct satellite, rocket body</li>
     * </ul>
     *
     * @param primary   the protected asset for which conjunction risk is computed and
     *                  avoidance actions may be planned
     * @param secondary the encounter object included only to assess collision risk;
     *                  no maneuver is assumed
     * @return a {@code ConjunctionRiskResult} containing probability and miss-distance metrics
     * @throws IllegalArgumentException if either object is {@code null}
     * @since 1.0
     */
    @PostMapping
    public ResponseEntity<String> submit(
            @RequestParam Long primary,
            @RequestParam Long secondary
    ) {
        Long assessmentId = assessmentService.submitAssessmentAsync(primary, secondary);
        return ResponseEntity.created(URI.create("/api/v1/assessments/" + assessmentId)).build();
    }

    @GetMapping
    public ResponseEntity<List<Long>> getAll() {
        return ResponseEntity.ok(assessmentService.getAssessmentIds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConjunctionAssessment> get(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessment(id));
    }
}
