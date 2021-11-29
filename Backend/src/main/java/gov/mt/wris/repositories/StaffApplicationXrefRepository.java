package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.models.StaffApplicationXref;

/**
 * @author Cesar.Zamorano
 *
 */
public interface StaffApplicationXrefRepository extends CrudRepository<StaffApplicationXref, BigDecimal> {
    public int deleteByApplicationId(BigDecimal appId);

    @Query(value = "SELECT sa\n" +
            "FROM StaffApplicationXref sa\n" +
            "JOIN FETCH sa.masterStaffIndex s\n" +
            "JOIN FETCH sa.application a\n" +
            "WHERE sa.applicationId = :appId",
            countQuery = "SELECT count(a) FROM StaffApplicationXref a WHERE a.applicationId = :appId")
    public Page<StaffApplicationXref> findApplicationsStaff(Pageable pageable, @Param("appId") BigDecimal appId);

    @Query(value = "SELECT MAX(sa.endDate)\n" +
                    "FROM StaffApplicationXref sa\n" +
                    "WHERE sa.applicationId = :appId")
    public LocalDate getLatestEndDate(@Param("appId") BigDecimal appId);

    @Query(value = "SELECT Count(sa)\n" +
                "FROM StaffApplicationXref sa\n" +
                "WHERE sa.applicationId = :appId\n" +
                "AND sa.endDate is null")
    public int countActiveStaff(@Param("appId") BigDecimal appId);

    @Query(value = "SELECT MIN(sa.createdDate)\n" +
                    "FROM StaffApplicationXref sa\n" +
                    "WHERE sa.applicationId = :appId\n")
    public LocalDateTime minCreatedDate(@Param("appId") BigDecimal appId);
}
