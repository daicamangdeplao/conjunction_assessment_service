package org.codenot.ssa.dto;

import lombok.Builder;

@Builder
public record OrbitUpdateMessage(
        Long objectId,
        Double semiMajorAxis,
        Double eccentricity
) {
}
