package gov.mt.wris.repositories;

import gov.mt.wris.models.DistrictCourtCase;
import gov.mt.wris.models.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface DistrictCourtCaseRepository extends JpaRepository<DistrictCourtCase, BigDecimal> {

    public Page<DistrictCourtCase> findAllDistrictCourtCaseByCaseId(Pageable pageable, BigDecimal caseId);

    @Query(value = "SELECT e\n"+
            " FROM Event e\n" +
            " LEFT JOIN FETCH e.districtCourtCase dc\n" +
            " LEFT JOIN FETCH e.eventType et \n" +
            " WHERE dc.caseId = :caseId \n" +
            " AND dc.districtId = :districtId \n",
            countQuery = "SELECT COUNT(e) FROM Event e JOIN DistrictCourtCase dc WHERE dc.caseId = :caseId AND dc.districtId = :districtId"
    )
    public Page<Event> findDistrictCourtEvents(Pageable pageable, BigDecimal caseId, BigDecimal districtId);

    public Optional<DistrictCourtCase> getDistrictCourtCaseByCaseIdAndDistrictId(BigDecimal caseId, BigDecimal districtId);

    @Modifying
    @Transactional
    public int deleteDistrictCourtCaseByCaseIdAndDistrictId(BigDecimal caseId, BigDecimal districtId);

}
