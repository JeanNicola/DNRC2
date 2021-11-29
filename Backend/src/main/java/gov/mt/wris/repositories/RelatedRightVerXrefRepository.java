package gov.mt.wris.repositories;

import gov.mt.wris.models.RelatedRight;
import gov.mt.wris.models.RelatedRightVerXref;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import java.math.BigDecimal;

@Repository
public interface RelatedRightVerXrefRepository extends JpaRepository<RelatedRightVerXref, BigDecimal>, CustomRelatedRightRepository {

    public boolean existsRelatedRightVerXrefByRelatedRightId(BigDecimal relatedRightId);

    @Query(value = "SELECT related\n" +
                    "FROM RelatedRight related\n" +
                    "JOIN related.relatedRightVerXref xref\n" +
                    "WHERE xref.waterRightId = :waterRightId\n" +
                    "AND xref.versionId = :versionId")
    public Page<RelatedRight> findRelatedRightsByWaterRightIdAndVersionId(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId, @Param("versionId") BigDecimal versionId);

    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
            "FROM RelatedRightVerXref r\n" +
            "WHERE r.relatedRightId = :relatedRightId and r.waterRightId = :waterRightId and r.versionId = :versionId \n")
    public int deleteByRelatedRightIdAndWaterRightId(@Param("relatedRightId") BigDecimal relatedRightId, @Param("waterRightId") BigDecimal waterRightId, @Param("versionId") BigDecimal versionId);

    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
            "FROM RelatedRightVerXref r\n" +
            "WHERE r.relatedRightId = :relatedRightId \n")
    public int deleteAllRelatedRightWaterRightReferences(@Param("relatedRightId") BigDecimal relatedRightId);

    @Query(value = "SELECT COUNT(r) FROM RelatedRightVerXref r WHERE r.relatedRightId = :relatedRightId \n")
    public int countRelatedRightXrefByRelatedRightId(@Param("relatedRightId") BigDecimal relatedRightId);
}
