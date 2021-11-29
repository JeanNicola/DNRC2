package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.WaterRightGeocode;

@Repository
public interface WaterRightGeocodeRepository extends JpaRepository<WaterRightGeocode, BigDecimal> {
    public int deleteByWaterRightId(BigDecimal waterRightId);

    @Query(value = "SELECT g\n" +
                    "FROM WaterRightGeocode g\n" +
                    "JOIN FETCH g.geocode gc\n" +
                    "JOIN FETCH g.waterRight w\n" +
                    "JOIN FETCH w.waterRightStatus s\n" +
                    "JOIN FETCH w.waterRightType t\n" +
                    "WHERE g.geocodeId = :geocodeId",
            countQuery = "SELECT COUNT(w)\n" +
                        "FROM WaterRightGeocode g\n" +
                        "JOIN g.waterRight w\n" +
                        "WHERE g.geocodeId = :geocodeId")
    Page<WaterRightGeocode> findByGeocodeId(Pageable pageable, @Param("geocodeId") String geocodeId);

    @Query(value = "SELECT wg\n" +
                    "FROM WaterRightGeocode wg\n" +
                    "JOIN FETCH wg.geocode g\n" +
                    "LEFT JOIN FETCH wg.createdByName c\n" +
                    "LEFT JOIN FETCH wg.modifiedByName m\n" +
                    "JOIN wg.waterRight w\n" +
                    "WHERE w.waterRightId = :waterRightId",
                countQuery = "SELECT COUNT(wg)\n" +
                                "FROM WaterRightGeocode wg\n" +
                                "JOIN wg.waterRight w\n" +
                                "WHERE w.waterRightId = :waterRightId")
    Page<WaterRightGeocode> findByWaterRightId(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT g.geocodeId\n" +
                "FROM WaterRightGeocode g\n" +
                "WHERE g.waterRightId = :waterRightId\n" +
                "AND g.geocodeId in :geocodeList\n")
    List<String> getDuplicateGeocodes(@Param("waterRightId") BigDecimal waterRightId, @Param("geocodeList") List<String> geocodeList);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM WaterRightGeocode g\n" +
                    "WHERE g.waterRightId = :waterRightId\n" +
                    "AND g.valid <> 'Y'\n" +
                    "AND (g.sever <> 'Y'\n" +
                        "OR (g.sever = 'Y' AND g.comments is null)\n" +
                    ")\n")
    int deleteInvalidGeocodes(@Param("waterRightId") BigDecimal waterRightId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE WaterRightGeocode g\n" +
                    "SET g.unresolved = 'Y'\n" +
                    "WHERE g.waterRightId = :waterRightId\n")
    int unresolveGeocodes(@Param("waterRightId") BigDecimal waterRightId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE WaterRightGeocode g\n" +
                    "SET g.sever = 'Y'\n" +
                    "WHERE g.waterRightId = :waterRightId\n")
    int severGeocodes(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT case when count(g) = 0 then true else false end\n" +
                    "FROM WaterRightGeocode g\n" +
                    "WHERE g.waterRightId = :waterRightId\n" +
                    "AND g.unresolved <> 'Y'\n")
    boolean allUnresolved(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT case when count(g) = 0 then true else false end\n" +
                    "FROM WaterRightGeocode g\n" +
                    "WHERE g.waterRightId = :waterRightId\n" +
                    "AND g.sever <> 'Y'\n")
    boolean allSevered(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT case when count(g) = 0 then true else false end\n" +
                    "FROM WaterRightGeocode g\n" +
                    "WHERE g.waterRightId = :waterRightId\n" +
                    "AND g.valid <> 'Y'\n" +
                    "AND (g.sever <> 'Y'\n" +
                        "OR (g.sever = 'Y' AND g.comments is null)\n" +
                    ")\n")
    boolean allValid(@Param("waterRightId") BigDecimal waterRightId);

    @Query(
        value =
            "SELECT CASE WHEN COUNT(geocode) > 0 THEN TRUE ELSE FALSE END\n" +
            "FROM WaterRightGeocode geocode\n" +
            "JOIN geocode.waterRight.versions version\n" +
            "JOIN version.applications application\n" +
            "WHERE application.id = :applicationId"
    )
    public boolean existsByApplicationId(@Param("applicationId") BigDecimal applicationId);

    @Query(value = "SELECT rv_meaning\n" +
                    "FROM WRD_REF_CODES\n" +
                    "WHERE rv_domain = 'WEB_URL'\n" +
                    "AND rv_low_value = 'GEOCODE_PREFIX'\n",
            nativeQuery = true)
    String getGeocodeUrl();

    @Query(value = "SELECT rv_meaning\n" +
                    "FROM WRD_REF_CODES\n" +
                    "WHERE rv_domain = 'WEB_URL'\n" +
                    "AND rv_low_value = 'NRIS_MAP_PREFIX'\n",
            nativeQuery = true)
    String getNRISMapURL();
}
