package gov.mt.wris.repositories;

import gov.mt.wris.models.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExaminationRepository extends JpaRepository<Examination, BigDecimal>, CustomExaminationRepository {

    @Query(value = "" +
            "SELECT SUM(pou.acreage) \n" +
            "FROM PlaceOfUse pou \n" +
            "JOIN pou.purpose pur \n" +
            "where pur.waterRightId = :waterRightId \n" +
            "and pur.versionId = :version \n" +
            "and pur.purposeTypeCode = 'IR' \n"
    )
    public BigDecimal getPurposeTotalAcreageIr(@Param("waterRightId") BigDecimal waterRightId, @Param("version") BigDecimal version);

    @Query(value = "" +
            "SELECT SUM(pou.acreage) \n" +
            "FROM PlaceOfUse pou \n" +
            "JOIN pou.purpose pur \n" +
            "where pur.waterRightId = :waterRightId \n" +
            "and pur.versionId = :version \n"
    )
    public BigDecimal getPurposeTotalAcreage(@Param("waterRightId") BigDecimal waterRightId, @Param("version") BigDecimal version);


    @Query( value = "SELECT e\n"
            + "FROM Examination e\n"
            + "LEFT JOIN FETCH e.pouExaminations pouExam\n"
            + "WHERE e.examinationId = :examinationId \n")
    public Optional<Examination> findById(@Param("examinationId") BigDecimal examinationId);
}
