package org.codenot.ssa.repository;

import org.codenot.ssa.domain.SpaceObjectJPAEntity;
import org.codenot.ssa.domain.constant.OperationalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceObjectRepository extends JpaRepository<SpaceObjectJPAEntity, Long> {
    List<Long> findAllSpaceObjectIds();
    List<Long> findAllSpaceObjectIdsByOperationalStatus(OperationalStatus operationalStatus);
}
