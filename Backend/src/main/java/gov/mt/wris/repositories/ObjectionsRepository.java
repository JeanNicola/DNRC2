package gov.mt.wris.repositories;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.ApplicationCaseSummary;
import gov.mt.wris.models.Objection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Base repository for Objections Table.
 *
 * @author Cesar.Zamorano
 */
@Repository
public interface ObjectionsRepository extends JpaRepository<Objection, BigDecimal> {

	/**
	 * @param pageable
	 * @param applicationId
	 * @return
	 */
	@Query( value = "SELECT o\n"
			+ "FROM Objection o\n"
			+ "LEFT JOIN FETCH o.statusReference s\n"
			+ "JOIN FETCH o.typeReference t\n"
			+ "LEFT JOIN FETCH o.lateReference l\n"
			+ "WHERE o.applicationId = :applicationId \n",
			countQuery = "SELECT COUNT(o) FROM Objection o WHERE o.applicationId = :applicationId")
	public Page<Objection> getObjections(Pageable pageable, @Param("applicationId") BigDecimal applicationId);

	@Query( value = "SELECT o\n"
			+ "FROM Objection o\n"
			+ "LEFT JOIN FETCH o.statusReference s\n"
			+ "JOIN FETCH o.typeReference t\n"
			+ "LEFT JOIN FETCH o.lateReference l\n"
			+ "WHERE o.waterRightId = :waterRightId AND o.versionId = :versionId \n",
			countQuery = "SELECT COUNT(o) FROM Objection o WHERE o.waterRightId = :waterRightId AND o.versionId = :versionId")
	public Page<Objection> getWaterRightVersionObjections(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId, @Param("versionId") BigDecimal versionId);

	@Query(value = "SELECT wcc.id as caseId, ct.description as caseTypeDescription, cst.description as caseStatusDescription\n" +
					"FROM CourtCase wcc\n" +
					"join wcc.caseType ct\n" +
					"left join wcc.caseStatus cst\n" +
					"WHERE wcc.id = (\n" +
						"SELECT MIN(cax.caseId)\n" +
						"FROM CaseApplicationXref cax\n" +
						"WHERE cax.applicationId = :applicationId\n" +
					")\n")
	public Optional<ApplicationCaseSummary> getFirstCase(@Param("applicationId") BigDecimal applicationId);

	public BigDecimal countByWaterRightId(BigDecimal waterRightId);

	public BigDecimal countByApplicationId(BigDecimal applicationId);

    @Query( value = " SELECT o \n" +
        " FROM Objection o\n" +
		" LEFT JOIN FETCH o.decree d\n" +
        " LEFT JOIN FETCH d.decreeType dt\n" +
        " LEFT JOIN FETCH o.waterRightVersion v\n" +
        " LEFT JOIN FETCH v.waterRight wr\n" +
        " LEFT JOIN FETCH o.typeReference tr\n" +
        " LEFT JOIN FETCH o.lateReference lr\n" +
        " LEFT JOIN FETCH o.statusReference sr\n" +
        " WHERE (:objectionId IS NULL OR str(o.id) LIKE :objectionId) \n" +
        " AND (:objectionType IS NULL OR o.type LIKE :objectionType) \n" +
        " AND (:filedDate IS NULL OR o.dateReceived LIKE :filedDate) \n" +
        " AND (:objectionLate IS NULL OR o.objectionLate LIKE :objectionLate) \n" +
		" AND (:objectionStatus IS NULL OR o.status LIKE :objectionStatus) \n" +
		" AND (:basin IS NULL OR d.basin LIKE :basin) \n",
        countQuery = " SELECT COUNT(o) FROM Objection o LEFT JOIN o.decree d WHERE (:objectionId IS NULL OR str(o.id) LIKE :objectionId) AND (:objectionType IS NULL OR o.type LIKE :objectionType) AND (:filedDate IS NULL OR o.dateReceived LIKE :filedDate) AND (:objectionLate IS NULL OR o.objectionLate LIKE :objectionLate) AND (:objectionStatus IS NULL OR o.status LIKE :objectionStatus) AND (:basin IS NULL OR d.basin LIKE :basin)"
    )
	public Page<Objection> searchObjections(Pageable pageable, @Param("objectionId") String objectionId, @Param("objectionType") String objectionType, @Param("filedDate") LocalDate filedDate, @Param("objectionLate") String objectionLate, @Param("objectionStatus") String objectionStatus, @Param("basin") String basin);

	@Query( value = " SELECT o\n"
			+ " FROM Objection o\n"
			+ " LEFT JOIN FETCH o.statusReference s\n"
			+ " JOIN FETCH o.typeReference t\n"
			+ " LEFT JOIN FETCH o.lateReference l\n"
			+ " WHERE o.waterRightId = :waterRightId \n"
			+ " AND o.versionId = :versionId \n",
			countQuery = "SELECT COUNT(o) FROM Objection o WHERE o.waterRightId = :waterRightId AND o.versionId = :versionId")
	public Page<Objection> findObjectionsByCaseAndWaterRightVersion(Pageable pageable, BigDecimal waterRightId, BigDecimal versionId);

	@Transactional
	@Modifying
	@Query(value = " UPDATE Objection o SET o.status = '" + Constants.CASE_STATUS_CLOSED + "' \n" +
	" WHERE o.waterRightId IN (SELECT xref.waterRightId FROM CourtCaseVersionXref xref WHERE xref.caseId = :caseId)")
	public int closeCaseObjections(BigDecimal caseId);

}
