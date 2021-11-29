package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.models.WaterRightType;

public interface WaterRightTypeRepository extends JpaRepository<WaterRightType, String> {
    @Query(value = "SELECT wt\n" +
                    "FROM WaterRightType wt\n" +
                    "WHERE wt.code in :typeList\n")
    public List<WaterRightType> findInList(@Param("typeList") List<String> typeList);

    public List<WaterRightType> findAllByOrderByDescriptionAsc();
}
