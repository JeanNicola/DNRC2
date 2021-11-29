package gov.mt.wris.repositories;

import gov.mt.wris.models.Application;
import gov.mt.wris.models.IdClasses.OwnerId;
import gov.mt.wris.models.Owner;
import gov.mt.wris.models.RepresentativeCount;
import gov.mt.wris.models.WaterRight;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Base Repository for Owners table.
 *
 * @author Cesar.Zamorano
 */
public interface OwnerRepository extends CrudRepository<Owner, OwnerId>, CustomOwnerRepository {
    /**
     * @param ownerId
     * @return
     */
    @Query(value = "SELECT o from Owner o WHERE o.ownerId = :ownerId")
    public Optional<Owner> getOwnerById(@Param("ownerId") BigDecimal ownerId);

    @Query(
        value =
            "SELECT o\n" +
            "FROM Owner o\n" +
            "JOIN FETCH o.customer c\n" +
            "WHERE o.ownerId = :ownerId\n" +
            "AND o.customerId = :customerId"
    )
    public Optional<Owner> findOwnersByOwnerIdAndCustomerId(
        @Param("ownerId") BigDecimal ownerId,
        @Param("customerId") BigDecimal customerId
    );

    @Transactional
    @Modifying
    @Query(value = "DELETE from Owner o WHERE o.ownerId= :ownerId")
    public void deleteById(@Param("ownerId") BigDecimal ownerId);

    public Boolean existsByCustomerIdAndApplicationIdAndEndDateIsNull(
        BigDecimal customerId,
        BigDecimal applicationId
    );

    @Query(
        value = "SELECT COUNT(o) FROM Owner o WHERE o.application.id= :appId"
    )
    public Long countOwnerByAppId(@Param("appId") BigDecimal appId);

    @Query(
        value =
            "SELECT o.waterRight\n" +
            "FROM Owner o\n" +
            "JOIN o.waterRight\n" +
            "WHERE o.customer.customerId = :customerId\n" +
            "AND o.endDate IS NULL",
        countQuery =
            "SELECT COUNT(o.waterRight.waterRightId)\n" +
            "FROM Owner o\n" +
            "JOIN o.waterRight\n" +
            "WHERE o.customer.customerId = :customerId\n" +
            "AND o.endDate IS NULL"
    )
    public Page<WaterRight> getCustomerWaterRights(
        Pageable pageable,
        BigDecimal customerId
    );

    @Query(
        value =
            "SELECT w\n" +
            "FROM WaterRight w\n" +
            "WHERE w.waterRightId IN (\n" +
                "SELECT o.waterRight.waterRightId\n" +
                "FROM Owner o\n" +
                "JOIN o.waterRight\n" +
                "JOIN o.waterRight.waterRightType\n" +
                "JOIN o.waterRight.waterRightStatus.typeXrefs typ\n" +
                "WHERE o.customer.customerId IN (:customerIds)\n" +
                "AND o.endDate IS NULL\n" +
                "AND typ.lovItem = 'Y'\n" +
                "AND typ.statusCode = o.waterRight.waterRightStatusCode\n" +
                "AND typ.typeCode = o.waterRight.waterRightTypeCode\n" +
                "AND (\n" +
                        "(:ouType = 'CD 608' AND o.waterRight.waterRightTypeCode = 'WRWR')\n" +
                        "OR (:ouType != 'CD 608' AND o.waterRight.waterRightTypeCode != 'WRWR')\n" +
                    ")\n" +
                ")\n" +
            "AND w.waterRightId NOT IN (\n" +
                "SELECT ou.waterRightId\n" +
                "FROM WaterRighOwnshiptXref ou\n" +
                "WHERE ou.ownershipUpdateId = :ouId\n" +
            ")",
        countQuery =
            "SELECT COUNT(w.waterRightId)\n" +
            "FROM WaterRight w\n" +
            "WHERE w.waterRightId IN (\n" +
                "SELECT o.waterRight.waterRightId\n" +
                "FROM Owner o\n" +
                "JOIN o.waterRight\n" +
                "JOIN o.waterRight.waterRightType\n" +
                "JOIN o.waterRight.waterRightStatus.typeXrefs typ\n" +
                "WHERE o.customer.customerId IN (:customerIds)\n" +
                "AND o.endDate IS NULL\n" +
                "AND typ.lovItem = 'Y'\n" +
                "AND typ.statusCode = o.waterRight.waterRightStatusCode\n" +
                "AND typ.typeCode = o.waterRight.waterRightTypeCode\n" +
                "AND (\n" +
                        "(:ouType = 'CD 608' AND o.waterRight.waterRightTypeCode = 'WRWR')\n" +
                        "OR (:ouType != 'CD 608' AND o.waterRight.waterRightTypeCode != 'WRWR')\n" +
                    ")\n" +
                ")\n" +
            "AND w.waterRightId NOT IN (\n" +
                "SELECT ou.waterRightId\n" +
                "FROM WaterRighOwnshiptXref ou\n" +
                "WHERE ou.ownershipUpdateId = :ouId\n" +
            ")"
    )
    public Page<WaterRight> getCustomersWaterRights(
        Pageable pageable,
        List<BigDecimal> customerIds,
        String ouType,
        BigDecimal ouId
    );

    @Query(
        value =
            "SELECT o.application\n" +
            "FROM Owner o\n" +
            "JOIN o.application\n" +
            "WHERE o.customer.customerId = :customerId\n" +
            "AND o.endDate IS NULL",
        countQuery =
            "SELECT COUNT(o.application.id)\n" +
            "FROM Owner o\n" +
            "JOIN o.application\n" +
            "WHERE o.customer.customerId = :customerId\n" +
            "AND o.endDate IS NULL"
    )
    public Page<Application> getCustomerApplications(
        Pageable pageable,
        BigDecimal customerId
    );

    @Transactional
    @Modifying
    @Query(
        value =
            "DELETE FROM Owner o\n" +
            "WHERE o.ownerId || o.customerId IN (\n" +
                "SELECT o.ownerId || o.customerId FROM Owner o\n" +
                "JOIN o.application a\n" +
                "WHERE a.id = :appId\n" +
            ")"
    )
    public int deleteByApplicationId(@Param("appId") BigDecimal appId);

    @Transactional
    @Modifying
    @Query(
        value =
            "UPDATE FROM Representative r\n" +
            "SET r.endDate = :endDate\n" +
            "WHERE r.secondaryCustomerId = :custId\n" +
            "AND r.ownerId = :ownId\n" +
            "AND r.endDate IS NULL\n"
    )
    public int endDateRepresentatives(
        @Param("custId") BigDecimal custId,
        @Param("ownId") BigDecimal ownId,
        @Param("endDate") LocalDate endDate
    );

    public Long countByApplicationIdAndEndDateIsNull(
        @Param("applicationId") BigDecimal applicationId
    );

    @Query(
        value =
            "SELECT MAX(r.endDate)\n" +
            "FROM Owner o\n" +
            "JOIN o.representatives r\n" +
            "WHERE o.ownerId = :ownId\n" +
            "AND o.customerId = :custId\n"
    )
    public Optional<LocalDate> getLatestRepresentativeEndDate(
        @Param("custId") BigDecimal custId,
        @Param("ownId") BigDecimal ownId
    );

    @Query(
        value =
            "SELECT o\n" +
            "FROM Owner o\n" +
            "JOIN o.waterRight w\n" +
            "JOIN FETCH o.customer c\n" +
            "LEFT JOIN FETCH o.contractReference contract\n" +
            "LEFT JOIN FETCH o.originReference origin\n" +
            "WHERE w.waterRightId = :waterRightId\n",
        countQuery =
            "SELECT count(o)\n" +
            "FROM Owner o\n" +
            "join o.waterRight w\n" +
            "WHERE w.waterRightId = :waterRightId\n"
    )
    public Page<Owner> findAllByWaterRightId(
        Pageable pageable,
        @Param("waterRightId") BigDecimal waterRightId
    );
}
