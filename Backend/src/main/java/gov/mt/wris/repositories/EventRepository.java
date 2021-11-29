package gov.mt.wris.repositories;

import gov.mt.wris.models.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, BigDecimal>, CustomEventRepository {

    @Query(value =
            "SELECT count(distinct appl_id_seq)\n" +
                    "    FROM wrd_event_dates\n" +
                    "    WHERE appl_id_Seq = :applicationId\n" +
                    "    and   evtp_cd = 'FRMR'\n" +
                    "    and dt_of_evnt > '19-AUG-2018'", nativeQuery = true)
    public Integer postChangeQueryCountFormReceived(@Param("applicationId") String appId);

    /**
     * UPDATE VERSION OPERATING AUTHORITY DATE AND VERSION STATUS FOR ALL WATER RIGHTS/VERSIONS
     * ASSOCIATED WITH THE APPLICATION
     *
     * @param date
     * @param appId
     */
    @Modifying
    @Query(value =
            "UPDATE WRD_VERSIONS VERS\n" +
                    "SET VERS.OPER_AUTHORITY = :eventDate \n" +
                    ", VERS.WRST_CD = 'ACTV'\n" +
                    "WHERE concat(VERS.WRGT_ID_SEQ, VERS.VERS_ID_SEQ) IN\n" +
                    "(SELECT concat(VAX.WRGT_ID_SEQ, VAX.VERS_ID_SEQ)\n" +
                    "FROM WRD_VERSION_APPLICATION_XREFS VAX\n" +
                    "WHERE VAX.APPL_ID_SEQ = :applicationId)", nativeQuery = true)
    public void postInsertQueryUpdateDateAndStatus(@Param("eventDate") Date date, @Param("applicationId") String appId);

    /**
     * Update priority and enforcable priority date when AME2 event added to a 600 app.
     *
     * @param date
     * @param appId
     * @return
     */
    @Modifying
    @Query(value =
            " UPDATE WRD_VERSIONS WVERS \n" +
                    " SET WVERS.PRTY_DT = :eventDate \n" +
                    " ,WVERS.ENF_PRTY_DT = :eventDate \n" +
                    " WHERE WVERS.WRGT_ID_SEQ IN ( \n" +
                    " SELECT VAX.WRGT_ID_SEQ\n" +
                    " FROM WRD_VERSION_APPLICATION_XREFS VAX\n" +
                    "  , WRD_WATER_RIGHTS WR\n" +
                    "  , WRD_EVENT_DATES ED\n" +
                    " WHERE VAX.APPL_ID_SEQ = :applicationId\n" +
                    " AND VAX.WRGT_ID_SEQ = WR.WRGT_ID_SEQ\n" +
                    " AND VAX.APPL_ID_SEQ = ED.APPL_ID_SEQ\n" +
                    "  AND ED.EVTP_CD = 'AME2'\n" +
                    " AND VAX.VERS_ID_SEQ = 1)\n" +
                    " AND   VERS_ID_SEQ = 1", nativeQuery = true)
    public Integer postInsertUpdatePriorityDate(@Param("eventDate") Date date, @Param("applicationId") String appId);

    @Query(value = "" +
            "select e\n" +
            "from Event e \n" +
            "join fetch e.eventType et\n" +
            "left join fetch e.createdByName crt\n" +
            "left join fetch e.modifiedByName mod\n" +
            "where e.appId=:applicationId\n",
            countQuery = "select count(e) from Event e where e.appId=:applicationId\n"
    )
    public Page<Event> findAllByAppId(Pageable pageable, @Param("applicationId") BigDecimal appId);

    public boolean existsEventByAppIdAndEventTypeCode(BigDecimal appId, String evtpCd);

    public Optional<Event> findByAppIdAndEventId(BigDecimal appId, BigDecimal eventId);

    @Query(value = "" +
            "SELECT e \n" +
            "FROM Event e \n" +
            "WHERE e.appId=:applicationId \n" +
            "AND e.eventTypeCode=:eventTypeCode \n"
    )
    public Optional<Event> findByAppIdAndEventTypeCode(@Param("applicationId") BigDecimal appId, @Param("eventTypeCode") String eventTypeCode);

    public boolean existsEventsByAppIdAndEventTypeCodeAndEventDateAfter(BigDecimal applicationId, String evtpCd, LocalDateTime dateOfEvent);

    public int deleteByApplicationId(BigDecimal appId);


     @Transactional
     @Modifying
     @Query(value =
         " INSERT INTO WRD_EVENT_DATES(EVTP_CD, APPL_ID_SEQ, DT_OF_EVNT) \n" +
         " SELECT 'OBRR', :applicationId, :filedDate FROM DUAL \n" +
         " WHERE NOT EXISTS (SELECT NULL FROM WRD_EVENT_DATES e WHERE e.EVTP_CD = 'OBRR' AND e.APPL_ID_SEQ = :applicationId )",
         nativeQuery = true
     )
     public int createApplicationObjectionEvent(@Param("applicationId") BigDecimal applicationId, @Param("filedDate") LocalDate filedDate);

    @Query(value = "" +
            " SELECT e \n" +
            " FROM Event e \n" +
            " JOIN FETCH e.eventType et \n" +
            " JOIN FETCH e.courtCase cc \n" +
            " LEFT JOIN FETCH e.createdByName crt \n" +
            " WHERE cc.id = :caseId \n",
            countQuery = "SELECT COUNT(e) FROM Event e JOIN e.courtCase cc WHERE cc.id = :caseId \n"
    )
    public Page<Event> findAllByCaseId(Pageable pageable, @Param("caseId") BigDecimal caseId);

    @Query(value = "" +
            " SELECT e \n" +
            " FROM Event e \n" +
            " JOIN FETCH e.courtCase cc \n" +
            " JOIN FETCH e.eventType et \n" +
            " LEFT JOIN FETCH e.createdByName crt \n" +
            " WHERE e.eventId = :eventId AND cc.id = :caseId \n"
    )
    public Optional<Event> getByEventIdAndCaseId(@Param("eventId") BigDecimal eventId, @Param("caseId") BigDecimal caseId);

    @Modifying
    @Transactional
    public int deleteEventByEventIdAndCourtCaseId(@Param("eventId") BigDecimal eventId, @Param("courtCaseId") BigDecimal courtCaseId);

    @Query(value =
        " SELECT e\n" +
        " FROM Event e \n" +
        " JOIN FETCH e.eventType et \n" +
        " LEFT JOIN FETCH e.districtCourtCase dc \n" +
        " WHERE e.districtId = :districtId \n" +
        " AND e.eventId = :eventId"
    )
    public Optional<Event> getDistrictCourtEvent(BigDecimal districtId, BigDecimal eventId);

    public int countEventByCaseIdAndDistrictId(BigDecimal caseId, BigDecimal districtId);

    @Modifying
    @Transactional
    public int deleteEventByDistrictIdAndEventId(BigDecimal districtId, BigDecimal eventId);

}
