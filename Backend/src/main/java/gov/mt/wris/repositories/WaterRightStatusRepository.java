package gov.mt.wris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.models.WaterRightStatus;

public interface WaterRightStatusRepository extends CrudRepository<WaterRightStatus, String>{
    @Query(
        value = "SELECT ws\n" +
                "FROM WaterRightStatus ws\n" +
                "JOIN FETCH ws.typeXrefs tx\n" +
                "WHERE tx.typeCode = :typeCode\n" +
                "order by ws.description"
    )
    List<WaterRightStatus> findByType(@Param("typeCode") String typeCode);
}