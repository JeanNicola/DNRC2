package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.models.WaterRightStaff;

public interface WaterRightStaffRepository extends JpaRepository<WaterRightStaff, BigDecimal> {
    public int deleteByWaterRightId(BigDecimal waterRightId);

    @Query(value = "SELECT ws\n" +
                    "FROM WaterRightStaff ws\n" +
                    "JOIN FETCH ws.staff s\n" +
                    "WHERE ws.waterRightId = :waterRightId\n",
            countQuery = "SELECT count(ws)\n" +
                        "FROM WaterRightStaff ws\n" +
                        "WHERE ws.waterRightId = :waterRightId")
    public Page<WaterRightStaff> findWaterRightStaff(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT MAX(ws.endDate)\n" +
                    "FROM WaterRightStaff ws\n" +
                    "WHERE ws.waterRightId = :waterRightId")
    public LocalDate getLatestEndDate(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT Count(ws)\n" +
                "FROM WaterRightStaff ws\n" +
                "WHERE ws.waterRightId = :waterRightId\n" +
                "AND ws.endDate is null")
    public int countActiveStaff(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT MIN(ws.createdDate)\n" +
                    "FROM WaterRightStaff ws\n" +
                    "WHERE ws.waterRightId = :waterRightId\n")
    public LocalDateTime minCreatedDate(@Param("waterRightId") BigDecimal waterRightId);

    @Query(value = "SELECT MIN(ws.beginDate)\n" +
                    "FROM WaterRightStaff ws\n" +
                    "WHERE ws.waterRightId = :waterRightId")
    public LocalDate minReceivedDate(@Param("waterRightId") BigDecimal waterRightId);
}
