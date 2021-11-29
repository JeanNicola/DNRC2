package gov.mt.wris.repositories;

import gov.mt.wris.models.PouExamUsgsMapXref;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

public interface PouExamUsgsMapXrefRepository extends JpaRepository<PouExamUsgsMapXref, BigDecimal> {

    @Query( value = ""
            + "SELECT pouExamUsgsXref\n"
            + "FROM PouExamUsgsMapXref pouExamUsgsXref\n"
            + "JOIN FETCH pouExamUsgsXref.usgs usgs\n"
            + "JOIN FETCH pouExamUsgsXref.pouExamination pouExam\n"
            + "WHERE pouExamUsgsXref.pexmId = :pexmId \n",
            countQuery = "SELECT COUNT(pouExamUsgsXref) FROM PouExamUsgsMapXref pouExamUsgsXref WHERE pouExamUsgsXref.pexmId = :pexmId"
    )
    public Page<PouExamUsgsMapXref> findByPexmId(Pageable pageable, BigDecimal pexmId);

    public Optional<PouExamUsgsMapXref> findByUtmpId(BigDecimal utmpId);

    @Modifying
    @Transactional
    public void deleteByUtmpIdAndPexmId(BigDecimal utmpId, BigDecimal pexmId);

    @Modifying
    @Transactional
    public void deleteByPexmId(BigDecimal pexmId);
}
