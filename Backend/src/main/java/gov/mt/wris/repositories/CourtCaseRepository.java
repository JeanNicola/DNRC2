package gov.mt.wris.repositories;

import gov.mt.wris.models.CourtCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtCaseRepository extends JpaRepository<CourtCase, BigDecimal>, CustomCourtCaseRepository {

    @Query(value = "SELECT cc\n"+
        " FROM CourtCase cc\n" +
        " JOIN FETCH cc.caseType ct \n" +
        " LEFT JOIN FETCH cc.caseStatus cs \n" +
        " LEFT JOIN FETCH cc.office o \n" +
        " LEFT JOIN FETCH cc.significantCaseReference sc \n" +
        " LEFT JOIN FETCH cc.courtCaseAssignToWc at \n" +
        " LEFT JOIN FETCH at.assignedTo msi1 \n" +
        " LEFT JOIN cc.courtCaseAssignToNa atna \n" +
        " LEFT JOIN atna.assignedTo msi2 \n" +
        " LEFT JOIN FETCH cc.caseApplicationXrefs ax \n" +
        " LEFT JOIN FETCH ax.application app\n" +
        " LEFT JOIN FETCH app.type appt\n" +
        " LEFT JOIN FETCH cc.decree d\n" +
        " LEFT JOIN FETCH d.decreeType dt\n" +
        " LEFT JOIN FETCH cc.decreeIssuedDate dis\n" +
        " WHERE cc.id = :caseId \n")
    public Optional<CourtCase> getCourtCase(BigDecimal caseId);

    public int countCourtCaseById(BigDecimal caseId);

    @Query(value = " SELECT DISTINCT cc.id\n"+
            " FROM CourtCase cc\n" +
            " WHERE cc.caseNumber = :waterCourtCaseNumber AND cc.id <> :caseId\n")
    public List<BigDecimal> waterCourtCaseNumberInUse(String waterCourtCaseNumber, BigDecimal caseId);

    @Query(value = " SELECT DISTINCT cc.id\n"+
            " FROM CourtCase cc\n" +
            " JOIN cc.caseApplicationXrefs apps \n" +
            " WHERE apps.applicationId = :applicationId AND cc.id <> :caseId\n")
    public List<BigDecimal> applicationNumberInUse(BigDecimal applicationId, BigDecimal caseId);

    @Query(value = " \n" +
            " SELECT DISTINCT WR.WTR_ID AS WATERRIGHTNUMBER, WR.EXT AS EXT, VR.VERS_ID_SEQ AS VERSION, WR.BOCA_CD AS BASIN, WR.WRGT_ID_SEQ, WRTE.DESCR, WRST.DESCR AS WATERRIGHTSTATUSDESCRIPTION, RC.RV_MEANING \n" +
            "     FROM WRD_DECREE_VERSION_XREFS DVX \n" +
            "     JOIN WRD_VERSIONS VR \n" +
            "       ON VR.WRGT_ID_SEQ = DVX.WRGT_ID_SEQ AND VR.VERS_ID_SEQ = DVX.VERS_ID_SEQ \n" +
            "     JOIN WRD_WATER_RIGHTS WR \n" +
            "       ON WR.WRGT_ID_SEQ = VR.WRGT_ID_SEQ \n" +
            "     JOIN WRD_WATER_RIGHT_STATUSES WRST \n" +
            "       ON WRST.WRST_CD = WR.WRST_CD \n" +
            "     JOIN WRD_WATER_RIGHT_TYPES WRTE \n" +
            "       ON WRTE.WRTE_CD = WR.WRTE_CD \n" +
            "     JOIN WRD_REF_CODES RC \n" +
            "       ON RC.RV_DOMAIN = 'VERSION TYPE' AND RC.RV_LOW_VALUE = VR.VER_TYP \n" +
            "     WHERE DVX.DECR_ID_SEQ = :decreeId \n" +
            "     AND (:waterNumber IS NULL OR TO_CHAR(WR.WTR_ID) LIKE :waterNumber) \n" +
            " UNION  \n" +
            " SELECT DISTINCT WR.WTR_ID WR_NUMBER, WR.EXT, VR.VERS_ID_SEQ VERSION, WR.BOCA_CD BASIN, WR.WRGT_ID_SEQ, WRTE.DESCR WR_TYPE, WRST.DESCR WR_STATUS, RC.RV_MEANING VR_TYPE \n" +
            "     FROM WRD_VERSIONS VR \n" +
            "     JOIN WRD_WATER_RIGHTS WR \n" +
            "       ON WR.WRGT_ID_SEQ = VR.WRGT_ID_SEQ \n" +
            "     JOIN WRD_WATER_RIGHT_STATUSES WRST \n" +
            "       ON WRST.WRST_CD = WR.WRST_CD \n" +
            "     JOIN WRD_WATER_RIGHT_TYPES WRTE \n" +
            "       ON WRTE.WRTE_CD = WR.WRTE_CD \n" +
            "     JOIN WRD_REF_CODES RC \n" +
            "       ON RC.RV_DOMAIN = 'VERSION TYPE' AND RC.RV_LOW_VALUE = VR.VER_TYP \n" +
            "     WHERE (:waterNumber IS NULL OR TO_CHAR(WR.WTR_ID) LIKE :waterNumber) \n" +
            "     AND (WR.BOCA_CD = :basin OR WR.SUB_BOCA_CD = :basin) \n" +
            "     AND WRTE.PROGRAM = 'ADJ' \n",
            nativeQuery = true,
            countQuery = "\n" +
            " SELECT COUNT(*) FROM ( \n" +
            "     SELECT DISTINCT WR.WTR_ID AS WATERRIGHTNUMBER, WR.EXT AS EXT, VR.VERS_ID_SEQ AS VERSION, WR.BOCA_CD AS BASIN, WR.WRGT_ID_SEQ, WRTE.DESCR, WRST.DESCR AS WATERRIGHTSTATUSDESCRIPTION, RC.RV_MEANING \n" +
            "         FROM WRD_DECREE_VERSION_XREFS DVX \n" +
            "         JOIN WRD_VERSIONS VR \n" +
            "           ON VR.WRGT_ID_SEQ = DVX.WRGT_ID_SEQ AND VR.VERS_ID_SEQ = DVX.VERS_ID_SEQ \n" +
            "         JOIN WRD_WATER_RIGHTS WR \n" +
            "           ON WR.WRGT_ID_SEQ = VR.WRGT_ID_SEQ \n" +
            "         JOIN WRD_WATER_RIGHT_STATUSES WRST \n" +
            "           ON WRST.WRST_CD = WR.WRST_CD \n" +
            "         JOIN WRD_WATER_RIGHT_TYPES WRTE \n" +
            "           ON WRTE.WRTE_CD = WR.WRTE_CD \n" +
            "         JOIN WRD_REF_CODES RC \n" +
            "           ON RC.RV_DOMAIN = 'VERSION TYPE' AND RC.RV_LOW_VALUE = VR.VER_TYP \n" +
            "         WHERE DVX.DECR_ID_SEQ = :decreeId \n" +
            "         AND (:waterNumber IS NULL OR TO_CHAR(WR.WTR_ID) LIKE :waterNumber) \n" +
            "     UNION  \n" +
            "     SELECT DISTINCT WR.WTR_ID WR_NUMBER, WR.EXT, VR.VERS_ID_SEQ VERSION, WR.BOCA_CD BASIN, WR.WRGT_ID_SEQ, WRTE.DESCR WR_TYPE, WRST.DESCR WR_STATUS, RC.RV_MEANING VR_TYPE \n" +
            "         FROM WRD_VERSIONS VR \n" +
            "         JOIN WRD_WATER_RIGHTS WR \n" +
            "           ON WR.WRGT_ID_SEQ = VR.WRGT_ID_SEQ \n" +
            "         JOIN WRD_WATER_RIGHT_STATUSES WRST \n" +
            "           ON WRST.WRST_CD = WR.WRST_CD \n" +
            "         JOIN WRD_WATER_RIGHT_TYPES WRTE \n" +
            "           ON WRTE.WRTE_CD = WR.WRTE_CD \n" +
            "         JOIN WRD_REF_CODES RC \n" +
            "           ON RC.RV_DOMAIN = 'VERSION TYPE' AND RC.RV_LOW_VALUE = VR.VER_TYP \n" +
            "         WHERE (:waterNumber IS NULL OR TO_CHAR(WR.WTR_ID) LIKE :waterNumber) \n" +
            "         AND (WR.BOCA_CD = :basin OR WR.SUB_BOCA_CD = :basin) \n" +
            "         AND WRTE.PROGRAM = 'ADJ' \n" +
            " ) \n"
    )
    public Page<Object[]> getEligibleWaterRights(Pageable pageable, String waterNumber, String decreeId, String basin);

}
