package gov.mt.wris.repositories;

import gov.mt.wris.models.CaseSchedule;
import gov.mt.wris.models.IdClasses.CaseScheduleId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CaseScheduleRepository extends JpaRepository<CaseSchedule, CaseScheduleId> {

    @Query(value = "\n" +
        " SELECT cs \n" +
        " FROM CaseSchedule cs \n" +
        " JOIN FETCH cs.eventType et \n" +
        " WHERE cs.caseId = :caseId",
        countQuery = "SELECT COUNT(cs) FROM CaseSchedule cs WHERE cs.caseId = :caseId"
    )
    public Page<CaseSchedule> findAllByCaseId(Pageable pageable, BigDecimal caseId);

    public Optional<CaseSchedule> findCaseScheduleByCaseIdAndScheduleId(@Param("caseId") BigDecimal caseId, @Param("scheduleId") BigDecimal scheduleId);

    @Transactional
    @Modifying
    public void deleteCaseScheduleByCaseIdAndScheduleId(BigDecimal caseId, BigDecimal scheduleId);

}
