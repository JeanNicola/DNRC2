package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.repositories.CustomAutoCompleteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.CallableStatement;
import java.sql.Types;

@Repository
public class CustomAutoCompleteRepositoryImpl implements CustomAutoCompleteRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(CustomAutoCompleteRepositoryImpl.class);

    @PersistenceContext
    EntityManager manager;

    @Override
    public int callAutoComplete(Long applicationId) {

        Session session = manager.unwrap(Session.class);
        Integer ac = session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection
                            .prepareCall(
                                    "{ ? = call WRD_COMMON_FUNCTIONS.AUTO_COMPLETE_APP(?) }")) {
                        callableStatement.registerOutParameter(1, Types.INTEGER);
                        callableStatement.setLong(2, applicationId);
                        callableStatement.execute();
                        return callableStatement.getInt(1);
                    }
                });
        return ac;

    }
}
