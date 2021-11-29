package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.models.OfficeApplicationXref;

/**
 * @author Cesar.Zamorano
 *
 */
public interface OfficeApplicationXrefRepository extends CrudRepository<OfficeApplicationXref, BigDecimal> {
    public int deleteByApplicationId(BigDecimal appId);

    @Query(value = "SELECT a\n" +
            "FROM OfficeApplicationXref a\n" +
            "JOIN FETCH a.office o\n" +
            "JOIN FETCH a.application app\n" +
            "WHERE a.applicationId = :appId",
            countQuery = "SELECT count(a) FROM OfficeApplicationXref a WHERE a.applicationId = :appId")
    public Page<OfficeApplicationXref> findApplicationsOffices(Pageable pageable, @Param("appId") BigDecimal appId);

    @Query(value = "SELECT MAX(oa.sentDate)\n" +
                    "FROM OfficeApplicationXref oa\n" +
                    "WHERE oa.applicationId = :appId")
    public LocalDate getLatestSentDate(@Param("appId") BigDecimal appId);

    @Query(value = "SELECT Count(oa)\n" +
                "FROM OfficeApplicationXref oa\n" +
                "WHERE oa.applicationId = :appId\n" +
                "AND oa.sentDate is null")
    public int countActiveOffices(@Param("appId") BigDecimal appId);

    @Query(value = "SELECT MIN(oa.createdDate)\n" +
                    "FROM OfficeApplicationXref oa\n" +
                    "WHERE oa.applicationId = :appId\n")
    public LocalDateTime minCreatedDate(@Param("appId") BigDecimal appId);
}
