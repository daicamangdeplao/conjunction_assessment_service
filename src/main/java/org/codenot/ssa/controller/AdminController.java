package org.codenot.ssa.controller;

import org.codenot.ssa.service.background.RoutineScreeningScheduler;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@Profile("dev")
public class AdminController {

    private final RoutineScreeningScheduler routineScreeningScheduler;

    public AdminController(RoutineScreeningScheduler routineScreeningScheduler) {
        this.routineScreeningScheduler = routineScreeningScheduler;
    }

    @GetMapping("/scheduler")
    public ResponseEntity<String> runSchedulerSync() {
        routineScreeningScheduler.submitRoutineScreeningSync();
        return ResponseEntity.ok("Scheduler is running");
    }
}
