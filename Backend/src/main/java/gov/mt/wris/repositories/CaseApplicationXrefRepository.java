package gov.mt.wris.repositories;

import gov.mt.wris.models.CaseApplicationXref;
import gov.mt.wris.models.IdClasses.CaseApplicationXrefId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Repository
public interface CaseApplicationXrefRepository extends JpaRepository<CaseApplicationXref, CaseApplicationXrefId> {

    @Transactional
    @Modifying
    @Query("delete from CaseApplicationXref cc where cc.caseId = :caseId")
    public void deleteByCaseId(@Param("caseId") BigDecimal caseId);
}