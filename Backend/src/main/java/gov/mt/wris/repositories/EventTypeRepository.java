package gov.mt.wris.repositories;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.ApplicationTypeXref;
import gov.mt.wris.models.CaseTypeXref;
import gov.mt.wris.models.DecreeTypeXref;
import gov.mt.wris.models.Event;
import gov.mt.wris.models.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// adding a custom pagination and filter function and the functions from CrudRepository
@Repository
public interface EventTypeRepository extends CrudRepository<EventType, String>, CustomEventTypeRepository {
    public Page<EventType> getEventTypes(Pageable pageable, String sortColumn, SortDirection sortDirection, String code, String description, String dueDays);

    @Query(value = "select count(a)\n" +
            "FROM ApplicationTypeXref a\n" +
            "where a.eventCode = :eventCode")
    int countApplicationXref(@Param("eventCode") String code);

    @Query(value = "select count(c)\n" +
            "FROM CaseTypeXref c\n" +
            "where c.eventCode = :eventCode")
    int countCaseXref(@Param("eventCode") String code);

    @Query(value = "select count(d)\n" +
            "FROM DecreeTypeXref d\n" +
            "where d.eventCode = :eventCode")
    int countDecreeXref(@Param("eventCode") String code);

    @Query(value = "select a\n" +
            "from ApplicationTypeXref a\n" +
            "left join fetch a.applicationType at\n" +
            "where a.eventCode = :eventCode\n" +
            "order by at.description")
    List<ApplicationTypeXref> getApplicationTypesByEventCode(@Param("eventCode") String eventCode);

    @Query(value = "select a\n" +
            "from ApplicationTypeXref a\n" +
            "left join fetch a.applicationType at\n" +
            "join fetch a.eventType e\n" +
            "where a.applicationCode = :applicationCode\n" +
            "order by e.description" +
            ""
    )
    List<ApplicationTypeXref> getEventCodeByApplicationType(@Param("applicationCode") String appTypeCode);


    @Query(value = "select c\n" +
            "from CaseTypeXref c\n" +
            "left join fetch c.caseType ct\n" +
            "where c.eventCode = :eventCode\n" +
            "order by ct.description")
    List<CaseTypeXref> getCaseTypesByEventCode(@Param("eventCode") String eventCode);

    @Query(value = "select d\n" +
            "from DecreeTypeXref d\n" +
            "left join fetch d.decreeType dt\n" +
            "where d.eventCode = :eventCode\n" +
            "order by dt.description")
    List<DecreeTypeXref> getDecreeTypesByEventCode(@Param("eventCode") String eventCode);

    @Query(value = " SELECT COALESCE(et.dueDays,0) \n" +
            " FROM EventType et \n" +
            " WHERE et.code = :eventCode \n"
    )
    public int getResponseDueDays(@Param("eventCode") String eventCode);

    @Query(value = "select ctx\n" +
            "from CaseTypeXref ctx\n" +
            "join fetch ctx.caseType c\n" +
            "join fetch ctx.eventType e\n" +
            "where ctx.caseCode = :typeCode\n" +
            "order by e.description" +
            ""
    )
    List<CaseTypeXref> getEventCodeByCaseType(@Param("typeCode") String typeCode);

}
