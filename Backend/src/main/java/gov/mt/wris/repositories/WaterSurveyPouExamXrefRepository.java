package gov.mt.wris.repositories;

import gov.mt.wris.models.WaterSurveyPouExamXref;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WaterSurveyPouExamXrefRepository extends JpaRepository<WaterSurveyPouExamXref, BigDecimal> {

    @Query( value = "SELECT wrsXref\n"
            + "FROM WaterSurveyPouExamXref wrsXref\n"
            + "JOIN FETCH wrsXref.waterResourceSurvey wrs\n"
            + "JOIN FETCH wrs.county c\n"
            + "WHERE wrsXref.pexmId = :pexmId \n",
            countQuery = "SELECT COUNT(wrsXref) FROM WaterSurveyPouExamXref wrsXref WHERE wrsXref.pexmId = :pexmId")
    public Page<WaterSurveyPouExamXref> findByPexmId(Pageable pageable, BigDecimal pexmId);

    @Query( value = "SELECT wrsXref\n"
            + "FROM WaterSurveyPouExamXref wrsXref\n"
            + "JOIN FETCH wrsXref.waterResourceSurvey wrs\n"
            + "JOIN FETCH wrs.county c\n"
            + "WHERE wrsXref.pexmId = :pexmId \n",
            countQuery = "SELECT COUNT(wrsXref) FROM WaterSurveyPouExamXref wrsXref WHERE wrsXref.pexmId = :pexmId")
    public List<WaterSurveyPouExamXref> findByPexmId(BigDecimal pexmId);

    @Query( value = "SELECT wrsXref\n"
            + "FROM WaterSurveyPouExamXref wrsXref\n"
            + "LEFT JOIN FETCH wrsXref.placesOfUse pou\n"
            + "JOIN FETCH wrsXref.pouExamination pouExam \n"
            + "JOIN FETCH pouExam.examination exam \n"
            + "WHERE wrsXref.pexmId = :pexmId \n"
            + "AND wrsXref.surveyId = :surveyId \n",
            countQuery = "SELECT COUNT(wrsXref) FROM WaterSurveyPouExamXref wrsXref WHERE wrsXref.pexmId = :pexmId AND wrsXref.surveyId = :surveyId")
    public Optional<WaterSurveyPouExamXref> findByPexmIdAndSurveyId(BigDecimal pexmId, BigDecimal surveyId);

}
