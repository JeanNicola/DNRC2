package gov.mt.wris.repositories;

import gov.mt.wris.models.IdClasses.VersionApplicationXrefId;
import gov.mt.wris.models.VersionApplicationXref;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VersionApplicationXrefRepository extends CrudRepository<VersionApplicationXref, VersionApplicationXrefId> {

    public List<VersionApplicationXref> getByApplicationIdOrderByWaterRightIdDesc(BigDecimal applicationId);

    public boolean existsByApplicationId(BigDecimal applicationId);

    public int deleteByApplicationId(BigDecimal appId);

    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
            "FROM VersionApplicationXref v\n" +
            "WHERE v.waterRightId = :waterRightId and v.versionId = :versionId and v.applicationId = :applicationId \n")
    public int deleteVersionApplicationXrefByWaterRightIdAndVersionIdAndApplicationId(@Param("waterRightId") BigDecimal waterRightId, @Param("versionId") BigDecimal versionId, @Param("applicationId") BigDecimal applicationId);

    public Optional<VersionApplicationXref> getVersionApplicationXrefByWaterRightIdAndVersionIdAndApplicationId(BigDecimal waterRightId, BigDecimal versionId, BigDecimal ApplicationId);
}
