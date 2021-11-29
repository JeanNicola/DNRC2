package gov.mt.wris.repositories;

import gov.mt.wris.dtos.ApplicationSearchResultDto;
import gov.mt.wris.dtos.ApplicationSortColumn;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends CrudRepository<Application, BigDecimal>,
                        CustomApplicationRepository{
    public Page<ApplicationSearchResultDto> getApplications(Pageable pageable,
                        ApplicationSortColumn sortDTOColumn,
                        DescSortDirection sortDirection,
                        String basin,
                        String applicationId,
                        String applicationTypeCode);

    @Query(value = "SELECT e\n" +
            "FROM Event e\n" +
            "WHERE e.appId = :applicationId\n" +
            "AND e.eventTypeCode = 'FRMR'\n" +
            "AND rownum = 1\n" +
            "order by e.eventDate"
    )
    public Optional<Event> getFormReceivedEvent(@Param("applicationId") BigDecimal id);

    @Query(value = "SELECT e\n" +
            "FROM Event e\n" +
            "WHERE e.appId = :applicationId\n" +
            "AND e.eventTypeCode = 'PAMH'\n" +
            "AND rownum = 1\n" +
            "order by e.eventDate"
    )
    public Optional<Event> getPreApplicationEvent(@Param("applicationId") BigDecimal id);


    @Query(value = ""+
        "SELECT a\n" +
        "FROM WRD_APPLICATIONS a\n " +
        "JOIN FETCH a.type b\n" +
        "JOIN FETCH a.dateTimeReceivedEvent e\n" +
        "WHERE a.regardingId = :applicationId\n",
        countQuery = "SELECT count(e) from WRD_APPLICATIONS e"
    )
    public Page<Application> findApplicationsByRegardingId(Pageable pageable, @Param("applicationId") BigDecimal id);

    public Optional<Application> getApplicationById(BigDecimal id);

    @Query(value = ""+
        "SELECT a\n" +
        "FROM WRD_APPLICATIONS a\n " +
        "JOIN FETCH a.type at\n" +
        "WHERE a.id = :applicationId")
    public Optional<Application> findApplicationsWithType(@Param("applicationId") BigDecimal id);

    @Query(value = "" +
        "SELECT SUM(at.FILING_FEE)\n" +
        "FROM WRD_EVENT_DATES e\n" +
        "join WRD_APPLICATION_TYPES at\n" +
        "on (at.APTP_CD = '607' and e.EVTP_CD = 'EXRD')\n" +
        "or (at.APTP_CD = '626' and e.EVTP_CD = 'RERD')\n" +
        "or (at.APTP_CD = '651' and e.EVTP_CD = 'MODR')\n" +
        "WHERE e.APPL_ID_SEQ = :applicationId\n",
        nativeQuery = true
    )
    public BigDecimal getFeesFromEvents(@Param("applicationId") BigDecimal id);

    @Query(value = ""+
        "SELECT a\n" +
        "FROM WRD_APPLICATIONS a\n " +
        "JOIN FETCH a.office o\n" +
        "WHERE a.id = :applicationId")
    public Optional<Application> findApplicationsWithOffice(@Param("applicationId") BigDecimal id);

    @Query(value = ""+
        "SELECT a\n" +
        "FROM WRD_APPLICATIONS a\n " +
        "LEFT JOIN FETCH a.processorOffice o\n" +
        "LEFT JOIN FETCH a.processorStaff s\n" +
        "WHERE a.id = :applicationId")
    public Optional<Application> findApplicationsWithProcessor(@Param("applicationId") BigDecimal id);

    @Query(value =
        " SELECT DISTINCT APPL  \n" +
        " FROM WRD_APPLICATIONS APPL, \n" +
        "      WaterRighOwnshiptXref WOUX, \n" +
        "      VersionApplicationXref VAX, \n" +
        "      Event EVDT \n" +
        " WHERE WOUX.waterRightId = VAX.waterRightId \n" +
        " AND VAX.applicationId = EVDT.application.id \n" +
        " AND APPL.id = EVDT.application.id \n" +
        " AND APPL.typeCode IN ('606', '634', '635', '644') \n" +
        " AND EVDT.eventTypeCode = 'ISSU' \n" +
        " AND WOUX.ownershipUpdateId = :ownerUpdateId \n" +
        " AND (:basin is null or APPL.basin LIKE :basin) \n" +
        " AND (:applicationId is null or str(APPL.id) LIKE :applicationId) \n" +
        " AND VAX.applicationId NOT IN (SELECT AOUX.applicationId FROM ApplicationOwnshipXref AOUX WHERE AOUX.ownershipUpdateId = :ownerUpdateId)"
    )
    public Page<Application> listChangeApplicationsForWaterRightsByOwnershipUpdate(Pageable pageable, @Param("applicationId") String applicationId, @Param("basin") String basin, @Param("ownerUpdateId") BigDecimal ownerUpdateId);

    @Query(value = ""+
            " SELECT a\n" +
            " FROM WRD_APPLICATIONS a\n " +
            " JOIN FETCH a.type at\n" +
            " JOIN FETCH at.objectionsAllowedReference oar\n" +
            " WHERE at.objectionsAllowed = 'Y' \n" +
            " AND (:applicationId IS NULL OR str(a.id) LIKE :applicationId) \n",
            countQuery = "SELECT COUNT(a) FROM WRD_APPLICATIONS a JOIN a.type at WHERE at.objectionsAllowed = 'Y' AND (:applicationId IS NULL OR str(a.id) LIKE :applicationId)"
    )
    public Page<Application> getObjectionsAllowedApplications(Pageable pageable, @Param("applicationId") String applicationId);

}
