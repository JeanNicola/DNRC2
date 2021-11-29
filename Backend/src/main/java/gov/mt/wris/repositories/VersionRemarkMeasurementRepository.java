package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.VersionRemarkMeasurement;

@Repository
public interface VersionRemarkMeasurementRepository extends JpaRepository<VersionRemarkMeasurement, BigDecimal> {
    public Page<VersionRemarkMeasurement> findByRemarkId(Pageable pageable, BigDecimal remarkId);
}
