package org.codenot.ssa.repository;

import org.codenot.ssa.domain.SpaceObjectJPAEntity;
import org.codenot.ssa.domain.constant.OperationalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceObjectRepository extends JpaRepository<SpaceObjectJPAEntity, Long> {
    @Query("SELECT so.id FROM SpaceObjectJPAEntity so")
    List<Long> findAllSpaceObjectIds();

    @Query("SELECT so.id FROM SpaceObjectJPAEntity so WHERE so.operationalStatus = :operationalStatus")
    List<Long> findAllSpaceObjectIdsByOperationalStatus(@Param("operationalStatus") OperationalStatus operationalStatus);
}
