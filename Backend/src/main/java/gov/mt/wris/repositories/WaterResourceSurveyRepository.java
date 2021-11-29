package gov.mt.wris.repositories;

import gov.mt.wris.models.WaterResourceSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface WaterResourceSurveyRepository extends JpaRepository<WaterResourceSurvey, BigDecimal> {

    @Query(value = "" +
            "SELECT wrs \n" +
            "FROM WaterResourceSurvey wrs \n" +
            "JOIN FETCH wrs.county county \n"
    )
    public List<WaterResourceSurvey> getAllWaterSurveyCounties();

}
