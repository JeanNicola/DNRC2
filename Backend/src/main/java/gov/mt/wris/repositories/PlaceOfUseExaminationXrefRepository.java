package gov.mt.wris.repositories;

import gov.mt.wris.models.IdClasses.PlaceOfUseExaminationXrefId;
import gov.mt.wris.models.PlaceOfUseExaminationXref;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PlaceOfUseExaminationXrefRepository extends JpaRepository<PlaceOfUseExaminationXref, PlaceOfUseExaminationXrefId> {

    @Modifying
    @Transactional
    @Query(value = "DELETE \n" +
            "FROM PlaceOfUseExaminationXref e\n" +
            "WHERE e.purposeId = :purposeId AND e.placeId = :placeId \n")
    public int deletePlaceOfUseExaminations(@Param("purposeId") BigDecimal purposeId, @Param("placeId") BigDecimal placeId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO PlaceOfUseExaminationXref (pexmId, purposeId, placeId) \n" +
                "SELECT pouExam.pexmId, :purposeId, :placeId \n" +
                "FROM PouExamination pouExam \n" +
                "WHERE pouExam.examinationId = \n" +
                "( \n" +
                    "SELECT MIN(e.examinationId) \n" +
                    "FROM Examination e \n" +
                    "WHERE e.purposeId = :purposeId \n" +
                ")" +
                "AND size(pouExam.placeOfUseExaminations) > 0 \n"
    )
    public int insertPlaceOfUseIntoAllDataSources(@Param("purposeId") BigDecimal purposeId, @Param("placeId") BigDecimal placeId);

    @Modifying
    @Query(value = "UPDATE \n" +
            "FROM PlaceOfUseExaminationXref e\n" +
            "SET e.surveyId = NULL \n" +
            "WHERE e.purposeId = :purposeId AND e.surveyId = :surveyId \n")
    public int removeSurveyFromPlaceOfUseExaminations(@Param("purposeId") BigDecimal purposeId, @Param("surveyId") BigDecimal surveyId);

    @Modifying
    @Query(value = "UPDATE \n" +
            "FROM PlaceOfUseExaminationXref e\n" +
            "SET e.aerialId = NULL \n" +
            "WHERE e.purposeId = :purposeId AND e.aerialId = :aerialId \n")
    public int removeAerialFromPlaceOfUseExaminations(@Param("purposeId") BigDecimal purposeId, @Param("aerialId") BigDecimal aerialId);

    @Query(value =  "SELECT pouExamXref \n" +
            "FROM PlaceOfUseExaminationXref pouExamXref \n" +
            "join fetch pouExamXref.placeOfUse pou \n" +
            "join fetch pou.legalLandDescription lld \n" +
            "join fetch pou.county c \n" +
            "join fetch lld.trs trs\n" +
            "WHERE pouExamXref.pexmId = :pexmId ",
            countQuery = "SELECT count(pouExamXref.pexmId) \n" +
                    "FROM PlaceOfUseExaminationXref pouExamXref \n" +
                    "WHERE pouExamXref.pexmId = :pexmId ")
    public Page<PlaceOfUseExaminationXref> findByPexmId(Pageable pageable, BigDecimal pexmId);

    public Optional<PlaceOfUseExaminationXref> findByPlaceIdAndPexmId(BigDecimal placeId, BigDecimal pexmId);

    @Modifying
    @Transactional
    public void deleteByPlaceIdAndPexmId(BigDecimal placeId, BigDecimal pexmId);

}
