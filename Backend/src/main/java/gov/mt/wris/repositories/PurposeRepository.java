package gov.mt.wris.repositories;

import gov.mt.wris.models.Purpose;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PurposeRepository extends JpaRepository<Purpose, BigDecimal> {

    @Query(value = "SELECT p \n" +
    " FROM Purpose p \n" +
    " JOIN FETCH p.waterRightVersion v \n" +
    " JOIN FETCH p.purposeType t \n" +
    " JOIN FETCH p.elementOriginReference r \n" +
    " LEFT JOIN FETCH p.climaticArea a \n" +
    " WHERE v.waterRight.basin LIKE :basin \n" +
    " AND (CAST(v.waterRight.waterRightNumber as string) LIKE :waterRightNumber) \n" +
    " AND v.waterRight.waterRightTypeCode LIKE :waterRightType \n" +
    " AND ((:versionType is null)OR(v.typeCode LIKE :versionType)) \n" +
    " AND ((:ext is null)OR(v.waterRight.ext LIKE :ext)) \n" +
    " AND ((:versionNumber is null)OR(CAST(v.version as string) LIKE :versionNumber)) \n",
    countQuery = "SELECT COUNT(p) FROM Purpose p JOIN p.waterRightVersion v WHERE v.waterRight.basin LIKE :basin AND (CAST(v.waterRight.waterRightNumber as string) LIKE :waterRightNumber) AND v.waterRight.waterRightTypeCode LIKE :waterRightType AND ((:versionType is null)OR(v.typeCode LIKE :versionType)) AND ((:versionNumber is null)OR(CAST(v.version as string) LIKE :versionNumber))")
    public Page<Purpose> searchPurposes(Pageable pageable, @Param("basin") String basin, @Param("waterRightNumber") String waterRightNumber, @Param("waterRightType") String waterRightType, @Param("ext") String ext, @Param("versionType") String versionType,@Param("versionNumber") String versionNumber);

    @Query(value = "SELECT p \n" +
            " FROM Purpose p \n" +
            " JOIN FETCH p.waterRightVersion v \n" +
            " JOIN FETCH v.waterRight w \n" +
            " JOIN FETCH w.waterRightType  wt \n" +
            " JOIN FETCH p.purposeType t \n" +
            " JOIN FETCH p.elementOriginReference r \n" +
            " LEFT JOIN FETCH p.purposeIrrigationXrefs x \n" +
            " LEFT JOIN FETCH p.climaticArea a \n" +
            " WHERE p.purposeId = :purposeId \n"
          )
    public Optional<Purpose> getPurpose(@Param("purposeId") BigDecimal purposeId);

    @Query(value = "SELECT COUNT(p2) \n" +
            " FROM Purpose p1, \n" +
            "      Purpose p2 \n" +
            " WHERE p1.purposeId = :purposeId \n" +
            " AND p2.waterRightId = p1.waterRightId \n" +
            " AND p2.versionId = p1.versionId \n" +
            " AND p2.purposeTypeCode = :purposeType \n"
    )
    public int findOtherPurposeCount(@Param("purposeId") BigDecimal purposeId, @Param("purposeType") String purposeType);

    public Optional<Purpose> findPurposeByWaterRightIdAndVersionIdAndPurposeTypeCode(BigDecimal waterRightId, BigDecimal versionId, String purposeTypeCode);

    @Query(value = " \n" +
            " SELECT V.MAX_ACRES AS ACRES \n" +
            " FROM WRD_PURPOSES P, WRD_VERSIONS V \n" +
            " WHERE P.purs_id_seq = :purposeId \n" +
            " AND V.WRGT_ID_SEQ = P.WRGT_ID_SEQ \n" +
            " AND V.VERS_ID_SEQ = P.VERS_ID_SEQ \n",
            nativeQuery = true
    )
    public Optional<BigDecimal> getWaterRightVersionMaxAcresByPurposeId(@Param("purposeId") BigDecimal purposeId);

    @Query(
            " SELECT COALESCE(SUM(p.volumeAmount),0) \n" +
            " FROM Purpose p \n" +
            " WHERE p.waterRightId = :waterRightId\n AND p.versionId = :versionId "
    )
    public BigDecimal getTotalPurposeVolumeForWaterRightVersion(@Param("waterRightId") BigDecimal waterRightId, @Param("versionId") BigDecimal versionId);

}
