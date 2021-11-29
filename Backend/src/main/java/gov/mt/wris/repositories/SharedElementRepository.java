package gov.mt.wris.repositories;

import gov.mt.wris.models.SharedElement;
import gov.mt.wris.models.IdClasses.SharedElementId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public interface SharedElementRepository extends JpaRepository<SharedElement, SharedElementId>, CustomSharedElementRepository {

    public Page<SharedElement> findAllByRelatedRightId(Pageable pageable, BigDecimal relatedRightId);

    public Optional<List<SharedElement>> findAllByRelatedRightId(BigDecimal relatedRightId);

    public boolean existsSharedElementByRelatedRightId(BigDecimal relatedRightId);

    public Optional<SharedElement> findByRelatedRightIdAndTypeCode(BigDecimal relatedRightId, String typeCode);

    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
            "FROM SharedElement s\n" +
            "WHERE s.relatedRightId = :relatedRightId and s.typeCode = :elementType \n")
    public int deleteByRelatedRightIdAndTypeCode(@Param("relatedRightId") BigDecimal relatedRightId, @Param("elementType") String elementType);

    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
            "FROM SharedElement s\n" +
            "WHERE s.relatedRightId = :relatedRightId \n")
    public int deleteByRelatedRightId(@Param("relatedRightId") BigDecimal relatedRightId);

    @Query(value = "SELECT COUNT(s) FROM SharedElement s WHERE s.relatedRightId = :relatedRightId \n")
    public int countSharedElementByRelatedRightId(@Param("relatedRightId") BigDecimal relatedRightId);

}
