package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.VersionRemark;

@Repository
public interface VersionRemarkRepository extends JpaRepository<VersionRemark, BigDecimal> {
    @Query(value = "SELECT r\n" +
    "FROM VersionRemark r\n" +
    "LEFT JOIN r.reportType rt\n" +
    "WHERE r.waterRightId = :waterRightId\n" +
    "AND r.version = :version\n" +
    "AND r.typeIndicator = 'C'")
    public Page<VersionRemark> findMeasurementReports(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId, @Param("version") BigDecimal version);

    @Query(value = "SELECT r\n" +
        "FROM VersionRemark r\n" +
        "JOIN FETCH r.remarkCodeLibrary rl\n" +
        "JOIN FETCH rl.categoryReference c\n" +
        "JOIN FETCH rl.typeReference t\n" +
        "JOIN FETCH rl.statusReference s\n" +
        "WHERE r.waterRightId = :waterRightId\n" +
        "AND r.version = :version\n" +
        "AND r.typeIndicator = 'R'",
        countQuery = "SELECT COUNT(r)\n" +
        "FROM VersionRemark r\n" +
        "WHERE r.waterRightId = :waterRightId\n" +
        "AND r.version = :version\n" +
        "AND r.typeIndicator = 'R'")
    public Page<VersionRemark> findRemarksByVersion(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId, @Param("version") BigDecimal version);

    public int countByWaterRightIdAndVersionAndRemarkCodeAndTypeIndicatorAndEndDateIsNull(BigDecimal waterRightId, BigDecimal version, String remarkCode, String typeIndicator);

    @Query(value = "SELECT COUNT(r) \n" +
        " FROM VersionRemark r \n" +
        " WHERE r.remarkCode = 'VA' \n" +
        " AND r.waterRightId = :waterRightId AND r.version = :versionId \n")
    public int combinedPurposeVolumeRemarkCount(@Param("waterRightId") BigDecimal waterRightId, @Param("versionId") BigDecimal versionId);
}
