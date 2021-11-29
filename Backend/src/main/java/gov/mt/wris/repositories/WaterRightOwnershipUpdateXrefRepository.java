package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.WaterRighOwnshiptXref;
import gov.mt.wris.models.IdClasses.WaterRighOwnshiptXrefId;

import javax.transaction.Transactional;

@Repository
public interface WaterRightOwnershipUpdateXrefRepository extends JpaRepository<WaterRighOwnshiptXref, WaterRighOwnshiptXrefId> {

    public int deleteByOwnershipUpdateId(BigDecimal ownershipUpdateId);

    public WaterRighOwnshiptXref findWaterRighOwnshiptXrefByOwnershipUpdateIdAndWaterRightId(BigDecimal ownershipUpdateId, BigDecimal waterRightId);

    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
            "FROM WaterRighOwnshiptXref rx\n" +
            "WHERE rx.ownershipUpdateId = :ownershipUpdateId and rx.waterRightId = :waterRightId \n")
    public int deleteByOwnershipUpdateIdAndWaterRightId(@Param("ownershipUpdateId") BigDecimal ownershipUpdateId, @Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT w.waterRightId\n" +
                    "FROM WaterRighOwnshiptXref w\n" +
                    "WHERE w.ownershipUpdateId = :ownershipUpdateId\n" +
                    "AND w.waterRightId in :waterRightIds")
    public List<BigDecimal> getWaterRightIds(BigDecimal ownershipUpdateId, List<BigDecimal> waterRightIds);
}