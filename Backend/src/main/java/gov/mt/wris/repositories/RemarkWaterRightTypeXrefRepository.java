package gov.mt.wris.repositories;

import gov.mt.wris.models.RemarkWaterRightTypeXref;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface RemarkWaterRightTypeXrefRepository extends JpaRepository<RemarkWaterRightTypeXref, String> {

    @Query(value =
            "SELECT x\n" +
            "FROM RemarkWaterRightTypeXref x\n" +
            "JOIN FETCH x.remarkCodeReference r\n" +
            "JOIN FETCH r.categoryReference c\n" +
            "JOIN FETCH c.elementTypeReference e\n" +
            "JOIN FETCH r.statusReference s\n" +
            "JOIN FETCH r.typeReference t\n" +
            "WHERE x.remarkCode LIKE :remarkCode\n" +
            "AND r.status <> 'ARCH'\n" +
            "AND x.code = (\n" +
                "SELECT w.waterRightTypeCode\n" +
                "FROM WaterRight w\n" + 
                "WHERE w.waterRightId = :waterRightId\n" +
            ")\n",
            countQuery =
            "SELECT COUNT(x)\n" +
            "FROM RemarkWaterRightTypeXref x\n" +
            "JOIN x.remarkCodeReference r\n" +
            "WHERE x.remarkCode LIKE :remarkCode\n" +
            "AND r.status <> 'ARCH'\n" +
            "AND x.code = (\n" +
                "SELECT w.waterRightTypeCode\n" +
                "FROM WaterRight w\n" + 
                "WHERE w.waterRightId = :waterRightId\n" +
            ")\n")
    public Page<RemarkWaterRightTypeXref> searchRemarkCodesByWaterRightType(Pageable pageable, @Param("remarkCode") String remarkCode, @Param("waterRightId") BigDecimal waterRightId);


}
