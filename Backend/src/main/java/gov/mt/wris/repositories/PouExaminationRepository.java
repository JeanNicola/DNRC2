package gov.mt.wris.repositories;

import gov.mt.wris.models.PouExamination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PouExaminationRepository extends JpaRepository<PouExamination, BigDecimal> {

    @Query( value = ""
            + "SELECT pouExam\n"
            + "FROM PouExamination pouExam\n"
            + "JOIN FETCH pouExam.dataSourceTotalAcres totalAcres\n"
            + "WHERE pouExam.examinationId = :examinationId \n",
            countQuery = "SELECT COUNT(pouExam) FROM PouExamination pouExam WHERE pouExam.examinationId = :examinationId"
    )
    public Page<PouExamination> findByExaminationId(Pageable pageable, BigDecimal examinationId);

    @Query( value = "SELECT pouExam\n"
            + "FROM PouExamination pouExam\n"
            + "JOIN FETCH pouExam.examination exam\n"
            + "JOIN FETCH pouExam.sourceTypeReference typeDescription \n"
            + "JOIN FETCH exam.purpose p\n"
            + "WHERE pouExam.pexmId = :pexmId \n")
    public Optional<PouExamination> findByIdAndFetchExaminationAndPurpose(BigDecimal pexmId);

}
