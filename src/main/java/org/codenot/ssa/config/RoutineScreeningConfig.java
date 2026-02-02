package org.codenot.ssa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutineScreeningConfig {

    @Value("${screening.scheduler.timeStepInMinute}")
    private int timeStepInMinute;
    @Value("${screening.scheduler.mode}")
    private String mode;

    @Bean
    public int timeStepInMinute() {
        return timeStepInMinute;
    }

    @Bean
    public String screeningMode() {
        return mode;
    }
}
