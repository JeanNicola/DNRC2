package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.MailingJobWaterRight;
import gov.mt.wris.models.IdClasses.MailingJobWaterRightId;

@Repository
public interface MailingJobWaterRightRepository extends JpaRepository<MailingJobWaterRight, MailingJobWaterRightId>, CustomMailingJobWaterRightRepository {
    @Transactional
    @Modifying
    public int deleteByMailingJobId(BigDecimal mailingJobId);

    @Query(value = "SELECT mw\n" +
        "FROM MailingJobWaterRight mw\n" +
        "JOIN FETCH mw.waterRight w\n" +
        "JOIN FETCH w.waterRightType t\n" +
        "LEFT JOIN FETCH w.waterRightStatus s\n" +
        "WHERE mw.mailingJobId = :mailingJobId",
    countQuery = "SELECT count(mw)\n" +
        "FROM MailingJobWaterRight mw\n" +
        "WHERE mw.mailingJobId = :mailingJobId")
    Page<MailingJobWaterRight> findByMailingJobId(Pageable pageable, BigDecimal mailingJobId);

    public Optional<MailingJobWaterRight> findByMailingJobIdAndWaterRightId(BigDecimal mailingJobId, BigDecimal waterRightId);

    @Transactional
    @Modifying
    public int deleteByMailingJobIdAndWaterRightId(BigDecimal mailingJobId, BigDecimal waterRightId);

    public int addMailingJobWaterRights(Long mailingJobId, HashMap<String, HashMap<String, List<String>>> waterRights);
}
