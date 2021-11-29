package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.repositories.CustomPeriodOfUseRepository;
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
public class CustomPeriodOfUseRepositoryImpl implements CustomPeriodOfUseRepository {

    public static Logger LOGGER = LoggerFactory.getLogger(CustomPeriodOfUseRepositoryImpl.class);

    @PersistenceContext
    EntityManager manager;

    /***
     * purposeId = id of purpose record to copy PODs to
     */
    public Integer copyPeriodOfDiversion(BigDecimal purposeId) {

        Session session = manager.unwrap(Session.class);
        Integer ac = session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection.prepareCall("{ ? = call WRD_COMMON_FUNCTIONS.copy_perdiv_to_peruse(?) }")) {
                        callableStatement.registerOutParameter(1, Types.INTEGER);
                        callableStatement.setBigDecimal(2, purposeId);
                        callableStatement.execute();
                        return callableStatement.getInt(1);
                    }
                }
        );
        return ac;

    }



}
