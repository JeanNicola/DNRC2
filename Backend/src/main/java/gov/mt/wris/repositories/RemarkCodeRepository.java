package gov.mt.wris.repositories;

import gov.mt.wris.models.Objection;
import gov.mt.wris.models.RemarkCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemarkCodeRepository extends JpaRepository<RemarkCode, String> {

    @Query(value = "\n" +
        " SELECT r \n" +
        " FROM RemarkCode r \n" +
        " JOIN FETCH r.categoryReference c \n" +
        " JOIN FETCH r.statusReference s \n" +
        " JOIN FETCH r.typeReference t \n" +
        " WHERE r.code LIKE :remarkCode \n",
            countQuery = "SELECT COUNT(r) FROM RemarkCode r WHERE r.code LIKE :remarkCode")
    public List<RemarkCode> searchRemarkCodes(@Param("remarkCode") String remarkCode);

    @Query(value = "SELECT r\n" +
            "FROM RemarkCode r\n" +
            "JOIN FETCH r.categoryReference c\n" +
            "WHERE r.basin is null\n" +
            "AND r.customerId is null\n" +
            "AND r.closureId is null\n" +
            "AND r.status = 'ACTV'\n" +
            "AND r.type = 'COND'\n" +
            "order by r.code")
    public List<RemarkCode> getMeasurmentReportRemarkCodes();

    @Query(value = "SELECT r\n" +
            "FROM RemarkCode r\n" +
            "JOIN FETCH r.categoryReference c\n" +
            "WHERE r.code in (\n" +
                "SELECT DISTINCT reports.remarkCode\n" +
                "FROM VersionRemark reports\n" +
                "WHERE reports.typeIndicator = 'C'\n" +
            ")\n" +
            "AND r.code not in (\n" +
                "SELECT rc.code\n" +
                "FROM RemarkCode rc\n" +
                "WHERE rc.basin is null\n" +
                "AND rc.customerId is null\n" +
                "AND rc.status = 'ACTV'\n" +
                "AND rc.type = 'COND'\n" +
            ")\n")
    public List<RemarkCode> getUsedMeasurementReportRemarkCodes();

}
