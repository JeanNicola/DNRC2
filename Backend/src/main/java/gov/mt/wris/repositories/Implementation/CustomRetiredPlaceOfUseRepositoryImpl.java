package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.repositories.CustomRetiredPlaceOfUseRepository;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;

@Repository
public class CustomRetiredPlaceOfUseRepositoryImpl implements CustomRetiredPlaceOfUseRepository {

    public static Logger LOGGER = LoggerFactory.getLogger(CustomPlaceOfUseRepositoryImpl.class);

    @PersistenceContext
    EntityManager manager;

    public BigDecimal getNextRetiredPlaceOfUseId(BigDecimal purposeId) {
        Session session = manager.unwrap(Session.class);
        Integer ac = session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection.prepareCall("{ ? = call WRD_FORM_UTILITIES.wrd_next_pusr_id_seq(?) }")) {
                        callableStatement.registerOutParameter(1, Types.INTEGER);
                        callableStatement.setBigDecimal(2, purposeId);
                        callableStatement.execute();
                        return callableStatement.getInt(1);
                    }
                }
        );
        return new BigDecimal(ac);
    }

    public Integer replicateRetPods(BigDecimal purposeId) {
        Session session = manager.unwrap(Session.class);
        Integer ac = session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection.prepareCall("{ ? = call WRD_COMMON_FUNCTIONS.replicate_pou_to_pou_retired(?) }")) {
                        callableStatement.registerOutParameter(1, Types.INTEGER);
                        callableStatement.setBigDecimal(2, purposeId);
                        callableStatement.execute();
                        return callableStatement.getInt(1);
                    }
                }
        );
        return ac;
    }

    /***
     * sortCode = "SEQ"; Eliminate Gaps
     * sortCode = "TRS"; Sort by TRS
     */
    public Integer reNumberRetPlaceOfUse(String sortCode, BigDecimal waterRightId, BigDecimal versionId) {
        Session session = manager.unwrap(Session.class);
        Integer ac = session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection.prepareCall("{ ? = call WRD_COMMON_FUNCTIONS.renumber_place_of_use_retired(?, ?, ?) }")) {
                        callableStatement.registerOutParameter(1, Types.INTEGER);
                        callableStatement.setString(2, sortCode);
                        callableStatement.setBigDecimal(3, waterRightId);
                        callableStatement.setBigDecimal(4, versionId);
                        callableStatement.execute();
                        return callableStatement.getInt(1);
                    }
                }
        );
        return ac;
    }
}
