package gov.mt.wris.repositories;

import gov.mt.wris.models.PeriodOfUse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodOfUseRepository extends JpaRepository<PeriodOfUse, BigDecimal>, CustomPeriodOfUseRepository {

    public List<PeriodOfUse> findDistinctAllByPurposeId(BigDecimal purposeId);

    @Query(value = "SELECT pou\n" +
        "FROM PeriodOfUse pou\n" +
        "JOIN FETCH pou.elementOriginReference r\n" +
        "WHERE pou.podId = :id",
        countQuery = "SELECT count(pou)\n" +
        "FROM PeriodOfUse pou\n" +
        "WHERE pou.podId = :id")
    public Page<PeriodOfUse> findbyPodId(Pageable pageable, BigDecimal id);

    public int countAllByPurposeId(BigDecimal purposeId);

    public  List<PeriodOfUse> findAllByPurposeId(BigDecimal purposeId);

    @Query(value = " \n" +
        " SELECT pou \n" +
        " FROM PeriodOfUse pou \n" +
        " LEFT JOIN FETCH pou.elementOriginReference eor \n" +
        " LEFT JOIN FETCH pou.waterRightVersion v \n" +
        " JOIN FETCH pou.purpose p \n" +
        " WHERE pou.purposeId = :purposeId \n",
        countQuery = "SELECT COUNT(pou) FROM PeriodOfUse pou WHERE pou.purposeId = :purposeId"
    )
    public Page<PeriodOfUse> getPeriodsOfUse(Pageable pageable, BigDecimal purposeId);

    Optional<PeriodOfUse> findPeriodOfUseByPeriodId(BigDecimal periodId);

    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
            "FROM PeriodOfUse p\n" +
            "WHERE p.periodId = :periodId \n")
    public int deletePeriodOfUse(@Param("periodId") BigDecimal periodId);

}
