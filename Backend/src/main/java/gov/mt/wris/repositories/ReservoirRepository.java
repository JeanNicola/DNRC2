package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.mt.wris.models.Reservoir;

@Repository
public interface ReservoirRepository extends JpaRepository<Reservoir, BigDecimal> {
    @Query(value = "SELECT r\n" +
                    "FROM Reservoir r\n" +
                    "join fetch r.originReference o\n" +
                    "join fetch r.county c\n" +
                    "left join fetch r.pointOfDiversion pod\n" +
                    "left join fetch r.legalLandDescription ll\n" +
                    "left join fetch ll.trs trs\n" +
                    "WHERE r.waterRightId = :waterRightId\n" +
                    "AND r.version = :version\n",
            countQuery = "SELECT COUNT(r)\n" +
                        "FROM Reservoir r\n" +
                        "WHERE r.waterRightId = :waterRightId\n" +
                        "AND r.version = :version\n")
    public Page<Reservoir> findReservoirs(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId, @Param("version") BigDecimal version);

    @Transactional
    public int deleteByIdAndWaterRightIdAndVersion(BigDecimal id, BigDecimal waterRightId, BigDecimal version);

    public int countReservoirByWaterRightIdAndVersion(BigDecimal waterRightId, BigDecimal version);
}
