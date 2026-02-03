package org.codenot.ssa.controller;

import org.codenot.ssa.dto.ConjunctionAssessment;
import org.codenot.ssa.service.AssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assessments")
public class AssessmentController {
    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConjunctionAssessment> get(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessment(id));
    }
}
