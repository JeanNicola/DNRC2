package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.models.Representative;

/**
 * @author Cesar.Zamorano
 */
public interface RepresentativesRepository
        extends CrudRepository<Representative, BigDecimal> {

    /**
     * @param pageable
     * @param ownerId
     * @return
     */
    @Query(value = "SELECT r\n" +
            "FROM Representative r\n" +
            "join fetch r.customer c\n " +
            "join fetch r.roleType t\n " +
            "WHERE r.ownerId = :ownerId\n",
            countQuery = "SELECT count(r)\n" +
                    "FROM Representative r\n" +
                    "WHERE r.ownerId = :ownerId\n")
    public Page<Representative> getRepresentatives(Pageable pageable, @Param("ownerId") BigDecimal ownerId);

    public Boolean existsByCustomerIdAndOwnerIdAndEndDateIsNull(BigDecimal customerId, BigDecimal ownerId);

    @Query(value = "SELECT r\n" +
            "FROM Representative r\n" +
            "join fetch r.customer c\n " +
            "WHERE r.objectionId = :objectionId\n" +
            "AND r.thirdCustomerId = :thirdCustomerId\n",
            countQuery = "SELECT count(r)\n" +
                    "FROM Representative r\n" +
                    "WHERE r.thirdCustomerId = :thirdCustomerId\n" +
                    "AND r.objectionId = :objectionId\n")
    public Page<Representative> findAllByObjectionIdAndThirdCustomerId(Pageable pageable, @Param("objectionId") BigDecimal objectionId, @Param("thirdCustomerId") BigDecimal thirdCustomerId);

    @Query(value = "SELECT r\n" +
                    "FROM Representative r\n" +
                    "JOIN FETCH r.customer c\n" +
                    "LEFT JOIN FETCH r.roleType rt\n" +
                    "WHERE r.ownerId = :ownerId\n" +
                    "AND r.secondaryCustomerId = :customerId",
                countQuery = "SELECT Count(r)\n" +
                            "FROM Representative r\n" +
                            "WHERE r.ownerId = :ownerId\n" +
                            "AND r.secondaryCustomerId = :customerId")
    public Page<Representative> findByOwnerIdAndSecondaryCustomerId(Pageable pageable, @Param("ownerId") BigDecimal ownerId, @Param("customerId") BigDecimal customerId);
}
