package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.models.OwnershipUpdateStaff;

public interface OwnershipUpdateStaffRepository extends JpaRepository<OwnershipUpdateStaff, BigDecimal> {
    @Query(value = "SELECT os\n" +
                    "FROM OwnershipUpdateStaff os\n" +
                    "JOIN FETCH os.staff s\n" +
                    "WHERE os.ownershipUpdateId = :ownershipUpdateId\n",
            countQuery = "SELECT count(os)\n" +
                        "FROM OwnershipUpdateStaff os\n" +
                        "WHERE os.ownershipUpdateId = :ownershipUpdateId")
    public Page<OwnershipUpdateStaff> findOwnershipUpdateStaff(Pageable pageable, @Param("ownershipUpdateId") BigDecimal ownershipUpdateId);

    @Query(value = "SELECT MAX(os.endDate)\n" +
                    "FROM OwnershipUpdateStaff os\n" +
                    "WHERE os.ownershipUpdateId = :updateId")
    public LocalDate getLatestEndDate(@Param("updateId") BigDecimal ownershipUpdateId);

    @Query(value = "SELECT Count(os)\n" +
                "FROM OwnershipUpdateStaff os\n" +
                "WHERE os.ownershipUpdateId = :updateId\n" +
                "AND os.endDate is null")
    public int countActiveStaff(@Param("updateId") BigDecimal ownershipUpdateId);

    @Query(value = "SELECT MIN(os.createdDate)\n" +
                    "FROM OwnershipUpdateStaff os\n" +
                    "WHERE os.ownershipUpdateId = :updateId\n")
    public LocalDateTime minCreatedDate(@Param("updateId") BigDecimal ownershipUpdateId);

    public int deleteByOwnershipUpdateId(BigDecimal ownershipUpdateId);
}
