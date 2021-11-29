package gov.mt.wris.repositories;

import gov.mt.wris.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PaymentRepository extends PagingAndSortingRepository<Payment, BigDecimal>{
    
    @Query(
        "SELECT coalesce(SUM(p.amount),0)\n" +
        "FROM Payment p\n" +
        "WHERE p.applicationId = :appId"
    )
    public double getFeesPaid(@Param("appId") BigDecimal appId);


    @Query(
            "SELECT coalesce(SUM(p.amount),0)\n" +
                    "FROM Payment p\n" +
                    "WHERE p.ownershipUpdateId = :ownId"
    )
    public double getFeesPaidForOwnershipUpdate(@Param("ownId") BigDecimal ownId);


    @Query(
        value = "SELECT p\n" +
        "FROM Payment p\n" +
        "left join fetch p.originReference o\n" +
        "WHERE p.applicationId = :appId",
        countQuery = "SELECT count(p)\n" +
                    "FROM Payment p\n" +
                    "WHERE p.applicationId = :appId"
    )
    public Page<Payment> findByApplicationId(Pageable pageable, @Param("appId") BigDecimal applicationId);

    @Query(
            value = "SELECT p\n" +
                    "FROM Payment p\n" +
                    "left join fetch p.originReference o\n" +
                    "WHERE p.ownershipUpdateId = :ouId",
            countQuery = "SELECT count(p)\n" +
                    "FROM Payment p\n" +
                    "WHERE p.ownershipUpdateId = :ouId"
    )
    public Page<Payment> findByOwnershipUpdateId(Pageable pageable, @Param("ouId") BigDecimal ownershipUpdateId);

    public int countByTrackingNo(String trackingNo);

    public int deleteByApplicationId(BigDecimal appId);
}
