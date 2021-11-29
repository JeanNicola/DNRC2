package gov.mt.wris.repositories;

import gov.mt.wris.models.CaseAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

public interface CaseAssignmentRepository extends JpaRepository<CaseAssignment, BigDecimal> {

    @Query(value = "" +
            " SELECT ca \n" +
            " FROM CaseAssignment ca \n" +
            " LEFT JOIN FETCH ca.masterStaffIndexes msi \n" +
            " JOIN FETCH ca.caseAssignmentType cat \n" +
            " WHERE ca.caseId = :caseId \n",
            countQuery = "SELECT COUNT(ca) FROM CaseAssignment ca WHERE ca.caseId = :caseId \n"
    )
    public Page<CaseAssignment> findAllByCaseId(Pageable pageable, @Param("caseId") BigDecimal caseId);

    @Transactional
    @Modifying
    public void deleteCaseAssignmentByCaseIdAndAssignmentId(BigDecimal caseId, BigDecimal assignmentId);

    @Query(value = "" +
            " SELECT ca \n" +
            " FROM CaseAssignment ca \n" +
            " LEFT JOIN FETCH ca.masterStaffIndexes msi \n" +
            " JOIN FETCH ca.caseAssignmentType cat \n" +
            " WHERE ca.caseId = :caseId AND ca.assignmentId = :assignmentId \n"
    )
    public Optional<CaseAssignment> findByCaseIdAndAssignmentId(@Param("caseId") BigDecimal caseId, @Param("assignmentId") BigDecimal assignmentId);

    public int countCaseAssignmentByCaseIdAndAssignmentTypeCodeAndEndDateIsNull(@Param("caseId") BigDecimal caseId, @Param("assignmentType") String assignmentType);

    public int countCaseAssignmentByCaseIdAndAssignmentTypeCodeAndAssignmentIdNotAndEndDateIsNull(@Param("caseId") BigDecimal caseId, @Param("assignmentType") String assignmentType, @Param("assignmentId") BigDecimal assignmentId);

}
