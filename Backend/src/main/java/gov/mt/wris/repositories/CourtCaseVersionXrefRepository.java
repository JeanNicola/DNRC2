package gov.mt.wris.repositories;

import gov.mt.wris.models.CourtCaseVersionXref;
import gov.mt.wris.models.IdClasses.CourtCaseVersionXrefId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtCaseVersionXrefRepository  extends JpaRepository<CourtCaseVersionXref, CourtCaseVersionXrefId> {

    @Query(value = "SELECT x \n" +
    " FROM CourtCaseVersionXref x \n" +
    " INNER JOIN FETCH x.courtCaseHearing c \n" +
    " WHERE x.waterRightId = :waterRightId \n" +
    " AND x.versionId = :versionId \n",
    countQuery = "SELECT count(x) FROM CourtCaseVersionXref x WHERE x.waterRightId = :waterRightId AND x.versionId = :versionId"
    )
    public Page<CourtCaseVersionXref> getWaterRightVersionCourtCases(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId, @Param("versionId") BigDecimal versionId);

    @Query(value = "SELECT xref \n" +
            " FROM CourtCaseVersionXref xref \n" +
            " JOIN FETCH xref.waterRightVersion wrv \n" +
            " JOIN FETCH wrv.versionStatus vs\n" +
            " JOIN FETCH wrv.typeReference vt\n" +
            " JOIN FETCH wrv.waterRight wr\n" +
            " JOIN FETCH wr.waterRightType wrt \n" +
            " JOIN FETCH wr.waterRightStatus wrs \n" +
            " WHERE xref.caseId = :caseId \n",
            countQuery = "SELECT count(xref) FROM CourtCaseVersionXref xref WHERE xref.caseId = :caseId"
    )
    public Page<CourtCaseVersionXref> getCaseWaterRightVersions(Pageable pageable, @Param("caseId") BigDecimal caseId);

    @Transactional
    @Modifying
    public int deleteCourtCaseVersionXrefByCaseIdAndWaterRightIdAndVersionId(BigDecimal caseId, BigDecimal waterRightId, BigDecimal versionId);

    @Query(value = "SELECT xref.caseId \n" +
            " FROM CourtCaseVersionXref xref \n" +
            " WHERE xref.waterRightId = :waterRightId \n" +
            " AND xref.caseId <> :caseId"
    )
    public List<BigDecimal> getWaterRightCaseId(BigDecimal waterRightId, BigDecimal caseId);

}
