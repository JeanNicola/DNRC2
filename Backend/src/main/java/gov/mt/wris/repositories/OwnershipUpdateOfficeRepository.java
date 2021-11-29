package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.models.OwnershipUpdateOffice;

public interface OwnershipUpdateOfficeRepository extends JpaRepository<OwnershipUpdateOffice, BigDecimal> {
    @Query(value = "SELECT ouo\n" +
                    "FROM OwnershipUpdateOffice ouo\n" +
                    "JOIN FETCH ouo.office o\n" +
                    "WHERE ouo.ownershipUpdateId = :ownershipUpdateId\n",
            countQuery = "SELECT count(ouo)\n" +
                        "FROM OwnershipUpdateOffice ouo\n" +
                        "WHERE ouo.ownershipUpdateId = :ownershipUpdateId")
    public Page<OwnershipUpdateOffice> findOwnershipUpdateOffices(Pageable pageable, @Param("ownershipUpdateId") BigDecimal ownershipUpdateId);

    @Query(value = "SELECT MAX(ouo.sentDate)\n" +
                    "FROM OwnershipUpdateOffice ouo\n" +
                    "WHERE ouo.ownershipUpdateId = :updateId")
    public LocalDate getLatestSentDate(@Param("updateId") BigDecimal ownershipUpdateId);

    @Query(value = "SELECT Count(ouo)\n" +
                "FROM OwnershipUpdateOffice ouo\n" +
                "WHERE ouo.ownershipUpdateId = :updateId\n" +
                "AND ouo.sentDate is null")
    public int countActiveOffices(@Param("updateId") BigDecimal ownershipUpdateId);

    @Query(value = "SELECT MIN(ouo.createdDate)\n" +
                    "FROM OwnershipUpdateOffice ouo\n" +
                    "WHERE ouo.ownershipUpdateId = :updateId\n")
    public LocalDateTime minCreatedDate(@Param("updateId") BigDecimal ownershipUpdateId);

    public int deleteByOwnershipUpdateId(BigDecimal ownershipUpdateId);
}
