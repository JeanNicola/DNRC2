package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.models.WaterRightOffice;

public interface WaterRightOfficeRepository extends JpaRepository<WaterRightOffice, BigDecimal> {
    public int deleteByWaterRightId(BigDecimal waterRightId);

    @Query(value = "SELECT wo\n" +
            "FROM WaterRightOffice wo\n" +
            "JOIN FETCH wo.office o\n" +
            "WHERE wo.waterRightId = :waterRightId",
            countQuery = "SELECT count(wo) FROM WaterRightOffice wo WHERE wo.waterRightId = :waterRightId")
    public Page<WaterRightOffice> findWaterRightsOffices(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT MAX(wo.sentDate)\n" +
                    "FROM WaterRightOffice wo\n" +
                    "WHERE wo.waterRightId = :waterRightId")
    public LocalDate getLatestSentDate(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT Count(wo)\n" +
                "FROM WaterRightOffice wo\n" +
                "WHERE wo.waterRightId = :waterRightId\n" +
                "AND wo.sentDate is null")
    public int countActiveOffices(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT MIN(wo.createdDate)\n" +
                    "FROM WaterRightOffice wo\n" +
                    "WHERE wo.waterRightId = :waterRightId\n")
    public LocalDateTime minCreatedDate(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT MIN(wo.receivedDate)\n" +
                    "FROM WaterRightOffice wo\n" +
                    "WHERE wo.waterRightId = :waterRightId")
    public LocalDate minReceivedDate(@Param("waterRightId") BigDecimal waterRightId);
}
