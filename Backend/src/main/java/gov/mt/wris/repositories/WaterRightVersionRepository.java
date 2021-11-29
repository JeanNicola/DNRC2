package gov.mt.wris.repositories;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.WaterRightVersionSortColumn;
import gov.mt.wris.models.IdClasses.WaterRightVersionId;
import gov.mt.wris.models.WaterRightVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WaterRightVersionRepository extends JpaRepository<WaterRightVersion, WaterRightVersionId>, CustomWaterRightVersionRepository{
    public Page<WaterRightVersion> getWaterRightVersions(Pageable pageable, WaterRightVersionSortColumn sortColumn, DescSortDirection sortDirection, Long waterRightId, String basin, String waterRightNumber, String versionNumber, String versionType);

    @Query(value = "SELECT wrv\n" +
                "FROM WaterRightVersion wrv\n" +
                "WHERE wrv.version = :version\n" +
                "AND wrv.waterRightId = :waterRightId")
    public Optional<WaterRightVersion> findById(@Param("waterRightId") BigDecimal waterRightId, @Param("version") BigDecimal version);

    @Query(value = "SELECT wrv\n" +
            " FROM WaterRightVersion wrv\n" +
            " JOIN FETCH wrv.waterRight w \n" +
            " JOIN FETCH w.waterRightType  wt \n" +
            " WHERE wrv.version = :version\n" +
            " AND wrv.waterRightId = :waterRightId")
    public Optional<WaterRightVersion> findByIdAndFetchWaterRight(@Param("waterRightId") BigDecimal waterRightId, @Param("version") BigDecimal version);

    @Query(
        value =
            "SELECT version\n" +
            "FROM WaterRightVersion version\n" +
            "LEFT JOIN FETCH version.county\n" +
            "WHERE version.waterRightId = :waterRightId\n" +
            "AND version.version = :version"
    )
    public Optional<WaterRightVersion> findByIdWithCounty(
        @Param("waterRightId") BigDecimal waterRightId,
        @Param("version") BigDecimal version
    );

    @Query(value = "SELECT wrv, size(wr.versions)\n" +
                    "FROM WaterRightVersion wrv\n" +
                    "JOIN wrv.applications a\n" +
                    "JOIN FETCH wrv.versionStatus vs\n" +
                    "JOIN FETCH wrv.typeReference vt\n" +
                    "JOIN FETCH wrv.waterRight wr\n" +
                    "JOIN FETCH wr.waterRightStatus wrs\n" +
                    "JOIN FETCH wr.waterRightType wrt\n" +
                    "WHERE a.id = :applicationId\n",
            countQuery = "SELECT count(wrv) FROM WRD_APPLICATIONS a\n" +
                            "JOIN a.waterRightVersions wrv\n" +
                            "WHERE a.id = :applicationId"
        )
    public Page<Object[]> getApplicationWaterRightVersions(Pageable pageable, @Param("applicationId") BigDecimal id);

    @Query(value = "SELECT count(wrv)\n" +
                    "FROM WaterRightVersion wrv\n" +
                    "JOIN wrv.applications a\n" +
                    "WHERE a.id = :applicationId\n")
    public int countByApplicationId(@Param("applicationId") BigDecimal appId);

    @Query(value = "SELECT wrv\n" +
                    "FROM WaterRightVersion wrv\n" +
                    "JOIN FETCH wrv.waterRight wr\n" +
                    "LEFT JOIN FETCH wrv.versionStatus vs\n" +
                    "LEFT JOIN FETCH wr.waterRightStatus wrs\n" +
                    "JOIN FETCH wr.waterRightType wrt\n" +
                    "JOIN FETCH wrv.typeReference vt\n" +
                    "LEFT JOIN FETCH wrv.volumeOriginReference\n" +
                    "LEFT JOIN FETCH wrv.acresOriginReference\n" +
                    "WHERE wrv.waterRightId = :waterRightId\n" +
                    "AND wrv.version = :versionId")
    public Optional<WaterRightVersion> findByWaterRightIdAndVersionId(@Param("waterRightId") BigDecimal waterRightId, @Param("versionId") BigDecimal versionId);

    @Query(value = "SELECT case when count(wrv) > 0 then true else false end\n" +
                    "FROM WaterRightVersion wrv\n" +
                    "JOIN wrv.applications a\n" +
                    "JOIN wrv.waterRight wr\n" +
                    "JOIN wrv.versionStatus vs\n" +
                    "JOIN wr.waterRightStatus wrs\n" +
                    "JOIN wr.waterRightType wrt\n" +
                    "JOIN wrv.typeReference vt\n" +
                    "WHERE wrv.waterRightId = :waterRightId\n" +
                    "AND wrv.version = :versionId\n" +
                    "AND a.id = :applicationId")
    public Boolean existsByApplicationIdAndWaterRightIdAndVersionId(@Param("applicationId") BigDecimal applicationId, @Param("waterRightId") BigDecimal waterRightId, @Param("versionId") BigDecimal versionId);

    @Query(value = "SELECT v\n" +
                    "FROM WaterRightVersion v\n" +
                    "JOIN FETCH v.typeReference t\n" +
                    "LEFT JOIN FETCH v.versionStatus s\n" +
                    "WHERE v.waterRightId = :waterRightId",
            countQuery = "SELECT COUNT(v)\n" +
                        "FROM WaterRightVersion v\n" +
                        "WHERE v.waterRightId = :waterRightId")
    public Page<WaterRightVersion> getVersionsOfWaterRight(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT case when count(pde) > 0 then true else false end\n" +
                    "FROM PointOfDiversionEnforcement pde\n" +
                    "JOIN pde.pointOfDiversion pd\n" +
                    "JOIN pd.version v\n" +
                    "WHERE v.waterRightId = :waterRightId")
    public boolean hasEnforcementAreas(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT case when count(v) > 0 then true else false end\n" +
                    "FROM WaterRightVersion v\n" +
                    "WHERE v.standardsApplied = 'Y'\n" +
                    "AND v.waterRightId = :waterRightId")
    public boolean hasStandardsApplied(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT case when Count(v) > 0 then true else false end\n" +
                    "FROM WaterRightVersion v\n" +
                    "WHERE v.typeCode = 'REXM'\n" +
                    "AND v.operatingAuthority is not null\n" +
                    "AND v.waterRightId = :waterRightId\n" +
                    "AND not EXISTS (\n" +
                        "SELECT d\n"+
                        "FROM DecreeVersion dx\n" +
                        "JOIN dx.decree d\n" +
                        "JOIN d.events e\n" +
                        "WHERE dx.waterRightId = v.waterRightId\n" +
                        "AND dx.versionNumber = v.version\n" +
                        "AND e.eventTypeCode = 'DISS'\n" +
                    ")\n")
    public boolean needsVersionDecree(@Param("waterRightId") BigDecimal waterRightId);

    @Procedure(procedureName = "WRD.WRD_SEND_MAIL", outputParameterName = "P_ERROR")
    Integer sendEmail(@Param("P_TO") String email1,
        @Param("P_TO2") String email2,
        @Param("P_TO3") String email3,
        @Param("P_TO4") String email4,
        @Param("P_FROM_TXT") String from_text,
        @Param("P_FROM_EMAIL") String from_email,
        @Param("P_SUBJECT") String subject,
        @Param("P_MESSAGE") String message);

    @Query(value = "SELECT MAX(v.version)\n" +
                    "FROM WaterRightVersion v\n" +
                    "WHERE v.waterRightId = :waterRightId")
    public Integer getNextVersionId(BigDecimal waterRightId);

    @Procedure(procedureName = "WRD_STANDARDS.RUN_STANDARDS")
    void runStandards(@Param("i_level") String level, @Param("i_typ") String type, @Param("i_decr_id_seq") BigDecimal decreeId, @Param("i_wrgt_id_seq") BigDecimal waterRightId, @Param("i_cust_id_seq") BigDecimal contactId, @Param("i_boca_cd") String basin);

    public int deleteByWaterRightIdAndVersion(BigDecimal waterRightId, BigDecimal version);

    @Query(value = " SELECT count(*) as COUNT \n" +
            " FROM wrd_decree_version_xrefs d, \n" +
            "      wrd_event_dates e \n" +
            " WHERE d.wrgt_id_seq = :waterRightId \n" +
            " AND d.vers_id_seq = :versionId \n" +
            " AND d.decr_id_seq = e.decr_id_seq \n" +
            " AND ((e.evtp_cd = 'DISS') OR (e.evtp_cd = 'SISS' AND e.rspns_due_dt IS NOT NULL)) \n",
            nativeQuery = true)
    public int isWaterRightVersionLocked(@Param("waterRightId") BigDecimal waterRightId, @Param("versionId") BigDecimal versionId);

    @Query("SELECT count(pod)\n" +
            "FROM PointOfDiversion pod\n" +
            "join pod.version wrv\n" +
            "WHERE wrv.waterRightId = :waterRightId\n" +
            "AND wrv.version = :version\n" +
            "AND pod.meansCode = 'WL'")
    public int countPODByWellCode(@Param("waterRightId") BigDecimal waterRightId, @Param("version") BigDecimal version);

    @Query("SELECT count(pod)\n" +
            "FROM PointOfDiversion pod\n" +
            "join pod.version wrv\n" +
            "WHERE wrv.waterRightId = :waterRightId\n" +
            "AND wrv.version = :version\n" +
            "AND pod.majorTypeCode = :majorType")
    public int countPODByMajorType(@Param("waterRightId") BigDecimal waterRightId, @Param("version") BigDecimal version, @Param("majorType") String majorType);

    @Query(value = " \n" +
            " SELECT V.MAX_ACRES AS ACRES \n" +
            " FROM WRD_PURPOSES P, WRD_VERSIONS V \n" +
            " WHERE P.purs_id_seq = :purposeId \n" +
            " AND V.WRGT_ID_SEQ = P.WRGT_ID_SEQ \n" +
            " AND V.VERS_ID_SEQ = P.VERS_ID_SEQ \n",
            nativeQuery = true
    )
    public Optional<BigDecimal> getWaterRightVersionMaxAcresByPurposeId(@Param("purposeId") BigDecimal purposeId);

    @Query("SELECT v.priorityDate FROM WaterRightVersion v WHERE v.waterRightId = :waterRightId AND v.version = :versionId")
    public Optional<LocalDate> getWaterRighVersionPriorityDate(@Param("waterRightId") BigDecimal waterRightId, @Param("versionId") BigDecimal versionId);

    @Query("SELECT a.applicationId\n" +
            "FROM VersionApplicationXref a\n" +
            "WHERE a.versionId = :version\n" +
            "AND a.waterRightId = :waterRightId")
    public List<BigDecimal> getApplicationIds(BigDecimal waterRightId, BigDecimal version);

    @Query("SELECT COUNT(a)\n" +
            "FROM VersionApplicationXref a\n" +
            "WHERE a.versionId = :version\n" +
            "AND a.waterRightId = :waterRightId")
    public int countApplications(BigDecimal waterRightId, BigDecimal version);

    @Query("SELECT v\n" +
        "FROM WaterRightVersion v\n" +
        "JOIN FETCH v.flowRateOriginReference fo\n" +
        "LEFT JOIN FETCH v.flowRateUnitReference fr\n" +
        "WHERE v.waterRightId = :waterRightId\n" +
        "AND v.version = :version")
    public Optional<WaterRightVersion> getVersionWithFlowRate(BigDecimal waterRightId, BigDecimal version);

    @Query("SELECT v\n" +
        "FROM WaterRightVersion v\n" +
        "JOIN FETCH v.waterRight w\n" +
        "WHERE v.waterRightId = :waterRightId\n" +
        "AND v.version = :version\n")
    public Optional<WaterRightVersion> findWithWaterRight(BigDecimal waterRightId, BigDecimal version);


    @Modifying
    @Transactional
    @Query(value = "UPDATE \n" +
            "FROM WaterRightVersion v\n" +
            "SET v.maximumVolume = :maxVolume \n" +
            "WHERE v.version = :version\n" +
            "AND v.waterRightId = :waterRightId")
    public int updateMaxVolume(BigDecimal waterRightId, BigDecimal version, BigDecimal maxVolume);

    @Query(value = "\n" +
            " SELECT DISTINCT WR.WTR_ID AS WATER_NUMBER, WR.BOCA_CD AS BASIN, VERS.VERS_ID_SEQ AS VERS_ID_SEQ, WR.WRGT_ID_SEQ AS WRGT_ID_SEQ, WR.EXT AS EXT, WRST.DESCR AS STATUS, WRTE.DESCR AS TYPE, VER_TYP_REF.RV_MEANING \n" +
            " FROM WRD_VERSIONS VERS \n" +
            "   JOIN WRD_WATER_RIGHTS WR \n" +
            "     ON WR.WRGT_ID_SEQ = VERS.WRGT_ID_SEQ \n" +
            "   JOIN WRD_WATER_RIGHT_TYPES WRTE \n" +
            "     ON WRTE.WRTE_CD = WR.WRTE_CD \n" +
            "   LEFT JOIN WRD_REF_CODES VER_TYP_REF \n" +
            "     ON VERS.VER_TYP = VER_TYP_REF.RV_LOW_VALUE \n" +
            "   JOIN WRD_WATER_RIGHT_STATUSES WRST \n" +
            "     ON WRST.WRST_CD = WR.WRST_CD \n" +
            " WHERE VERS.VER_TYP IN ('ORIG','POST','SPPD','SPLT')  \n" +
            " AND WRTE.PROGRAM IN ('ADJ','PR')  \n" +
            " AND WR.boca_cd = :basin \n" +
            " AND (:waterNumber IS NULL OR TO_CHAR(WR.WTR_ID) LIKE :waterNumber) \n" +
            " UNION \n" +
            " SELECT DISTINCT WR.WTR_ID AS WATER_NUMBER, WR.BOCA_CD AS BASIN, VERS.VERS_ID_SEQ AS VERS_ID_SEQ, WR.WRGT_ID_SEQ AS WRGT_ID_SEQ, WR.EXT AS EXT, WRST.DESCR AS STATUS, WRTE.DESCR AS TYPE, VER_TYP_REF.RV_MEANING \n" +
            " FROM WRD_VERSIONS VERS \n" +
            "   JOIN WRD_WATER_RIGHTS WR \n" +
            "     ON WR.WRGT_ID_SEQ = VERS.WRGT_ID_SEQ \n" +
            "   JOIN WRD_WATER_RIGHT_TYPES WRTE \n" +
            "     ON WRTE.WRTE_CD = WR.WRTE_CD \n" +
            "   LEFT JOIN WRD_REF_CODES VER_TYP_REF \n" +
            "     ON VERS.VER_TYP = VER_TYP_REF.RV_LOW_VALUE \n" +
            "   JOIN WRD_WATER_RIGHT_STATUSES WRST \n" +
            "     ON WRST.WRST_CD = WR.WRST_CD \n" +
            " WHERE (VERS.wrgt_id_seq, VERS.vers_id_Seq) in ( \n" +
            "       select dx.wrgt_id_Seq, dx.vers_id_Seq \n" +
            "        from wrd_decree_version_xrefs dx, wrd_decrees d \n" +
            "        where dx.decr_id_seq = d.decr_id_seq \n" +
            "        and d.boca_cd = :basin) \n" +
            " AND (:waterNumber IS NULL OR TO_CHAR(WR.WTR_ID) LIKE :waterNumber) \n",
            countQuery = "\n" +
            " SELECT COUNT(*) FROM (\n" +
            "    SELECT DISTINCT WR.WTR_ID AS WATER_NUMBER, WR.BOCA_CD AS BASIN, VERS.VERS_ID_SEQ AS VERS_ID_SEQ, WR.WRGT_ID_SEQ AS WRGT_ID_SEQ, WR.EXT AS EXT, WRST.DESCR AS STATUS, WRTE.DESCR AS TYPE, VER_TYP_REF.RV_MEANING \n" +
            "    FROM WRD_VERSIONS VERS \n" +
            "      JOIN WRD_WATER_RIGHTS WR \n" +
            "        ON WR.WRGT_ID_SEQ = VERS.WRGT_ID_SEQ \n" +
            "      JOIN WRD_WATER_RIGHT_TYPES WRTE \n" +
            "        ON WRTE.WRTE_CD = WR.WRTE_CD \n" +
            "      LEFT JOIN WRD_REF_CODES VER_TYP_REF \n" +
            "        ON VERS.VER_TYP = VER_TYP_REF.RV_LOW_VALUE \n" +
            "      JOIN WRD_WATER_RIGHT_STATUSES WRST \n" +
            "        ON WRST.WRST_CD = WR.WRST_CD \n" +
            "    WHERE VERS.VER_TYP IN ('ORIG','POST','SPPD','SPLT')  \n" +
            "    AND WRTE.PROGRAM IN ('ADJ','PR')  \n" +
            "    AND WR.boca_cd = :basin \n" +
            "    AND (:waterNumber IS NULL OR TO_CHAR(WR.WTR_ID) LIKE :waterNumber) \n" +
            "    UNION \n" +
            "    SELECT DISTINCT WR.WTR_ID AS WATER_NUMBER, WR.BOCA_CD AS BASIN, VERS.VERS_ID_SEQ AS VERS_ID_SEQ, WR.WRGT_ID_SEQ AS WRGT_ID_SEQ, WR.EXT AS EXT, WRST.DESCR AS STATUS, WRTE.DESCR AS TYPE, VER_TYP_REF.RV_MEANING \n" +
            "    FROM WRD_VERSIONS VERS \n" +
            "      JOIN WRD_WATER_RIGHTS WR \n" +
            "        ON WR.WRGT_ID_SEQ = VERS.WRGT_ID_SEQ \n" +
            "      JOIN WRD_WATER_RIGHT_TYPES WRTE \n" +
            "        ON WRTE.WRTE_CD = WR.WRTE_CD \n" +
            "      LEFT JOIN WRD_REF_CODES VER_TYP_REF \n" +
            "        ON VERS.VER_TYP = VER_TYP_REF.RV_LOW_VALUE \n" +
            "      JOIN WRD_WATER_RIGHT_STATUSES WRST \n" +
            "        ON WRST.WRST_CD = WR.WRST_CD \n" +
            "    WHERE (VERS.wrgt_id_seq, VERS.vers_id_Seq) in ( \n" +
            "          SELECT dx.wrgt_id_Seq, dx.vers_id_Seq \n" +
            "          FROM wrd_decree_version_xrefs dx, wrd_decrees d \n" +
            "          WHERE dx.decr_id_seq = d.decr_id_seq \n" +
            "          AND d.boca_cd = :basin)" +
            "    AND (:waterNumber IS NULL OR TO_CHAR(WR.WTR_ID) LIKE :waterNumber) \n" +
            " ) \n",
            nativeQuery = true
    )
    public Page<Object[]> getEligibleWaterRightVersions(Pageable pageable, @Param("basin") String basin, @Param("waterNumber") String waterNumber);
}
