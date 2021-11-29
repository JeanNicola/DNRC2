package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.LegalLandDescription;

@Repository
public interface LegalLandDescriptionRepository extends JpaRepository<LegalLandDescription, BigDecimal>, CustomLegalLandDescriptionRepository {
    public Long validateLegalLandDescription(
        String description320,
        String description160,
        String description80,
        String description40,
        Long governmentLot,
        Long township,
        String townshipDirection,
        Long range,
        String rangeDirection,
        Long section,
        Long countyId);
}
