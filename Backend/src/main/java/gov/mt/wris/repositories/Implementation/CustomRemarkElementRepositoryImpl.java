package gov.mt.wris.repositories.Implementation;

import java.sql.CallableStatement;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.mt.wris.repositories.CustomRemarkElementRepository;
import oracle.jdbc.OracleTypes;

public class CustomRemarkElementRepositoryImpl implements CustomRemarkElementRepository {
    private static Logger LOGGER = LoggerFactory.getLogger(CustomRemarkElementRepository.class);

    @PersistenceContext
    EntityManager manager;

    public Boolean validateAllowableVariableText(String tableName,
        String columnName,
        String value
    ) {
        LOGGER.info("Validating a Variable Value against a table");

        Session session = manager.unwrap(Session.class);

        Boolean valid = session.doReturningWork(
            connection -> {
                try (CallableStatement callableStatement = connection.prepareCall("{? = call WRD_COMMON_FUNCTIONS.VALIDATE_ALLOWABLE_VALUES(?, ?, ?)}")) {
                    callableStatement.registerOutParameter(1, OracleTypes.PLSQL_BOOLEAN);
                    callableStatement.setString(2, tableName);
                    callableStatement.setString(3, columnName);
                    callableStatement.setString(4, value);
                    callableStatement.execute();
                    return callableStatement.getBoolean(1);
                }
            }
        );

        return valid;
    }
}
