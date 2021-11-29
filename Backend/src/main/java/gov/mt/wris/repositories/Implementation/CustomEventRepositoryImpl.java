package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.models.Event;
import gov.mt.wris.models.EventType;
import gov.mt.wris.repositories.CustomEventRepository;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.CallableStatement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implemetation of EventRepository
 *
 * @author Vannara Houth
 */
@Repository
public class CustomEventRepositoryImpl implements CustomEventRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(CustomEventRepositoryImpl.class);

    @PersistenceContext
    EntityManager manager;


    /**
     * @param cb
     * @param root
     * @param join
     * @param eventId
     * @param applicationId
     * @param event
     * @param dateTime
     * @param responseDue
     * @param comments
     * @return
     */
    // private Predicate[] getPredicates(CriteriaBuilder cb, Root<Event> root, Join<Event, EventType> join, Join<Event, MasterStaffIndexes> join2, Long eventId, String applicationId, String event, LocalDateTime dateTime, LocalDateTime responseDue, String comments) {
    private Predicate[] getPredicates(CriteriaBuilder cb, Root<Event> root, Join<Event, EventType> join, Long eventId, String applicationId, String event, LocalDateTime dateTime, LocalDateTime responseDue, String comments) {

        List<Predicate> predicates = new ArrayList<>();
        if (eventId != null) {
            predicates.add(cb.equal(root.get("eventId"), eventId.toString()));
        }
        if (event != null) {
            predicates.add(cb.equal(join.get("code"), event));
        }
        if (comments != null) {
            predicates.add(cb.equal(root.get("eventComment"), comments));
        }
        if (dateTime != null) {
            predicates.add(cb.equal(root.get("eventDate"), dateTime));
        }
        if (responseDue != null) {
            predicates.add(cb.equal(root.get("responseDueDate"), responseDue));
        }
        if (applicationId != null) {
            predicates.add(cb.equal(root.get("appId"), applicationId));
        }

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;
    }


    @Override
    public int callReinstate(Long applicationId) {
        Session session = manager.unwrap(Session.class);
        Integer reinstate = session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection
                            .prepareCall(
                                    "{ ? = call WRD_COMMON_FUNCTIONS.REINSTATE_APPLICATION(?) }")) {
                        callableStatement.registerOutParameter(1, Types.INTEGER);
                        callableStatement.setLong(2, applicationId);
                        callableStatement.execute();
                        return callableStatement.getInt(1);
                    }
                });
        return reinstate;
    }


    @Override
    public String callDoesApplWrHaveGeocodeYn(String applicatioinId) {
        Session session = manager.unwrap(Session.class);
        String reinstate = session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection
                            .prepareCall(
                                    "{ ? = call WRD_COMMON_FUNCTIONS.DOES_APPL_WR_HAVE_GEOCODE_YN(?) }")) {
                        callableStatement.registerOutParameter(1, Types.NCHAR);
                        callableStatement.setString(2, applicatioinId);
                        callableStatement.execute();
                        return callableStatement.getString(1);
                    }
                });
        return reinstate;
    }


    /**
     * UPDATE OPERATING AUTHORITY DATE FOR ALL WATER RIGHTS/VERSIONS ATTACHED TO THE APPLICATION WITH THE
     * ISSUED DATE
     * UPDATE WATER RIGHT AND VERSION STATUSES TO ACTIVE FOR ALL WATER RIGHTS/VERSIONS ATTACHED TO
     * THE APPLICATION WITH THE ISSUED DATE
     *
     * @param applicationId
     * @return
     */
    @Override
    public String postInsertUpdateStatusToActive(String applicationId) {
        Session session = manager.unwrap(Session.class);
        StringBuilder query = new StringBuilder();

        String queryDeclare =
                "DECLARE\n" +
                        "UPDT_WR VARCHAR2(1) := 'T';\n" +
                        "Message VARCHAR2(2000) := 'UPDATED';\n" +
                        "MAX_VERS_OPDT NUMBER := 0;\n" +
                        "  CURSOR WR_CUR IS\n" +
                        "SELECT VAX.WRGT_ID_SEQ, VAX.VERS_ID_SEQ, WR.WTR_ID\n" +
                        "FROM WRD_VERSION_APPLICATION_XREFS VAX\n" +
                        ", WRD_WATER_RIGHTS WR\n" +
                        "WHERE VAX.APPL_ID_SEQ = ?\n" +
                        "AND VAX.WRGT_ID_SEQ = WR.WRGT_ID_SEQ;";

        String queryBody =
                "BEGIN " +
                        "FOR WR_REC IN WR_CUR\n" +
                        "LOOP\n" +
                        "SELECT MAX(V1.VERS_ID_SEQ)\n" +
                        "INTO MAX_VERS_OPDT\n" +
                        "FROM WRD_VERSIONS V1\n" +
                        "WHERE V1.WRGT_ID_SEQ = WR_REC.WRGT_ID_SEQ\n" +
                        "AND V1.OPER_AUTHORITY =\n" +
                        "(SELECT MAX(V2.OPER_AUTHORITY)\n" +
                        "FROM WRD_VERSIONS V2\n" +
                        "WHERE V2.WRGT_ID_SEQ = V1.WRGT_ID_SEQ);\n" +
                        "IF WR_REC.VERS_ID_SEQ = MAX_VERS_OPDT THEN\n" +
                        "UPDATE WRD_WATER_RIGHTS WR\n" +
                        "SET WR.WRST_CD = 'ACTV'\n" +
                        "WHERE WR.WRGT_ID_SEQ = WR_REC.WRGT_ID_SEQ;\n" +
                        "ELSE\n" +
                        "Message :=  'WATER RIGHT STATUS NOT ' || Message || ', VERSION DOES NOT HAVE THE LATEST OPERATING ' ||'AUTHORITY DATE - WATER RIGHT: ' || WR_REC.WTR_ID || '  VERSION: ' || WR_REC.VERS_ID_SEQ; \n" +
                        " END IF;\n" +
                        "\n" +
                        " END LOOP;" +
                        "?:= Message;" +
                        "END;";

        query.append(queryDeclare).append(queryBody);
        return session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection
                            .prepareCall(
                                    query.toString())) {
                        callableStatement.setLong(1, Long.parseLong(applicationId));
                        callableStatement.registerOutParameter(2, java.sql.Types.VARCHAR);
                        callableStatement.execute();
                        return callableStatement.getString(2);
                    }
                });

    }

}
