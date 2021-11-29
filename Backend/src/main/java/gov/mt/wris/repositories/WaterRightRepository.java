package gov.mt.wris.repositories;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.WaterRightSortColumn;
import gov.mt.wris.dtos.WaterRightVersionSearchSortColumn;
import gov.mt.wris.models.WaterRight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WaterRightRepository extends JpaRepository<WaterRight, BigDecimal>, CustomWaterRightRepository {

    @Query(value = "SELECT w\n" +
                    "FROM WaterRight w\n" +
                    "join fetch w.waterRightType  wt\n" +
                    "left join fetch w.waterRightStatus ws\n" +
                    "left join fetch w.originalWaterRight ow\n" +
                    "left join fetch ow.waterRightType owt\n" +
                    "left join fetch ow.waterRightStatus ows\n" +
                    "left join fetch w.subcompact sc\n" +
                    "left join fetch sc.compact c\n" +
                    "WHERE w.waterRightId = :waterRightId\n")
    public Optional<WaterRight> findByIdWithOriginal(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "" +
            "select w\n" +
            "from WaterRight w \n" +
            "join fetch w.waterRightStatus ws\n" +
            "join fetch w.waterRightType  wt\n" +
            "join fetch w.mailingJobRefs ref\n" +
            "where ref.mailingJobId=:mailingJobId\n" +
            "and ref.mailingJob.applicationId=:applicationId\n",
            countQuery = "" +
                    "select count(w) \n" +
                    "from WaterRight w \n" +
                    "left join w.mailingJobRefs ref\n" +
                    "where ref.mailingJobId=:mailingJobId\n" +
                    "and ref.mailingJob.applicationId=:applicationId\n"
            )
    public Page<WaterRight> findWaterRightsByMailingJobId(Pageable pageable, @Param("applicationId") BigDecimal id, @Param("mailingJobId") BigDecimal mailingJobId);

    boolean existsByWaterRightNumberAndBasin(BigDecimal waterRightNumber, String basin);

    public Page<WaterRight> getWaterRights(Pageable pageable,
        WaterRightSortColumn sortColumn,
        DescSortDirection sortDirection,
        String basin,
        String waterRightNumber,
        String ext,
        String typeCode,
        String statusCode,
        String subBasin,
        String waterReservationId,
        String conservationDistrictNumber);
    
    @Query(value = "SELECT WRD_APPL_SEQ.nextval\n" +
                "from dual",
            nativeQuery = true)
    public BigDecimal getNewWaterRightNumberForCreation();


    @Query(value = " SELECT DISTINCT DECODE(X.END_DT, NULL, (DECODE(X.VALID,'Y','1',NULL))) GEO_VALID \n" +
            "   FROM WRD_GEOCODE_WATER_RIGHT_XREFS X \n" +
            "   WHERE :waterRightId = X.WRGT_ID_SEQ(+) \n" +
            " MINUS \n" +
            " SELECT DISTINCT DECODE(X.VALID,'N','1',NULL) GEO_VALID \n" +
            "   FROM WRD_GEOCODE_WATER_RIGHT_XREFS X \n" +
            "   WHERE :waterRightId = X.WRGT_ID_SEQ(+) \n" +
            "   AND   X.END_DT IS NULL \n" +
            "   AND   X.VALID = 'N' \n",
            nativeQuery = true)
    public String checkValidGeocode(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT COUNT(v)\n" +
                    "FROM WaterRightVersion v\n" +
                    "JOIN v.typeReference t\n" +
                    "JOIN v.versionStatus s\n" +
                    "WHERE t.value like 'CHAU%'\n" +
                    "AND s.code = 'ACTV'\n" +
                    "AND v.waterRightId = :waterRightId")
    public int countChangeVersions(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT COUNT(c)\n" +
                    "FROM WaterRight c\n" +
                    "JOIN c.originalWaterRight o\n" +
                    "WHERE o.waterRightId = :waterRightId")
    public Long countChildRights(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT c\n" +
                    "FROM WaterRight c\n" +
                    "JOIN c.originalWaterRight o\n" +
                    "JOIN FETCH c.waterRightType t\n" +
                    "LEFT JOIN FETCH c.waterRightStatus s\n" +
                    "WHERE o.waterRightId = :waterRightId",
                countQuery = "SELECT COUNT(c)\n" +
                            "FROM WaterRight c\n" +
                            "JOIN c.originalWaterRight o\n" +
                            "WHERE o.waterRightId = :waterRightId")
    public Page<WaterRight> getChildRights(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId);

    public Page<Object[]> getWaterRightsWithChangeAuthorizationCount(Pageable pageable,
        WaterRightSortColumn sortColumn,
        DescSortDirection sortDirection,
        String basin,
        String waterRightNumber,
        String ext,
        String typeCode,
        String statusCode,
        String subBasin,
        String waterReservationId,
        String conservationDistrictNumber);

    public Page<Object[]> getWaterRightsByVersions(Pageable pageable,
        WaterRightVersionSearchSortColumn sortColumn,
        DescSortDirection sortDirection,
        String waterRightNumber,
        String version,
        String versionTypeMeaning);
    

    @Query(value = "SELECT case when Count(v) > 0 then true else false end\n" +
                    "FROM WaterRightVersion v\n" +
                    "JOIN v.decreeXrefs dx\n" +
                    "JOIN dx.decree d\n" +
                    "JOIN d.events e\n" +
                    "WHERE e.eventTypeCode = 'DISS'\n" +
                    "AND v.waterRightId = :waterRightId")
    public boolean needsDecree(@Param("waterRightId") BigDecimal waterRightId);


    @Query(value = "SELECT case when Count(v) > 0 then true else false end\n" +
                    "FROM WaterRightVersion v\n" +
                    "JOIN v.decreeXrefs dx\n" +
                    "JOIN dx.decree d\n" +
                    "JOIN d.events e\n" +
                    "WHERE (e.eventTypeCode = 'DISS' OR\n" +
                    "(e.eventTypeCode = 'SISS' AND e.responseDueDate is not null))\n" +
                    "AND v.waterRightId = :waterRightId")
    public boolean needsDecreePermission(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT w.waterRightId\n" +
                    "FROM WaterRight w\n" +
                    "WHERE w.waterRightId in :waterRightList")
    public List<BigDecimal> getWaterRightIds(@Param("waterRightList") List<BigDecimal> waterRightIds);

    @Query(value = "SELECT COALESCE(MAX(v.version), 1)\n" +
        "FROM WaterRightVersion v\n" +
        "WHERE v.waterRightId = :waterRightId\n" +
        "AND v.operatingAuthority = (\n" +
            "SELECT MAX(v2.operatingAuthority)\n" +
            "FROM WaterRightVersion v2\n" +
            "WHERE v2.waterRightId = :waterRightId\n" +
        ")")
    public BigDecimal getMostRecentOperatingVersion(BigDecimal waterRightId);
}
