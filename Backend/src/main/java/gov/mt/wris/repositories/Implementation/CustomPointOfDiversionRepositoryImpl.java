package gov.mt.wris.repositories.Implementation;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import gov.mt.wris.repositories.CustomPointOfDiversionRepository;
import oracle.jdbc.OracleTypes;

public class CustomPointOfDiversionRepositoryImpl implements CustomPointOfDiversionRepository {
    private static Logger LOGGER = LoggerFactory.getLogger(CustomPointOfDiversionRepository.class);

    @PersistenceContext
    private EntityManager manager;

    public int renumberPODs(String type,
        BigDecimal waterRightId,
        BigDecimal version
    ) {
        LOGGER.info("Sorting the PODs attached to a Version");

        Session session = manager.unwrap(Session.class);

        int sorted = session.doReturningWork(
            connection -> {
                try (CallableStatement callableStatement = connection.prepareCall("{? = call WRD_COMMON_FUNCTIONS.renumber_diversion(?, ?, ?)}")) {
                    callableStatement.registerOutParameter(1, Types.INTEGER);
                    callableStatement.setString(2, type);
                    callableStatement.setBigDecimal(3, waterRightId);
                    callableStatement.setBigDecimal(4, version);
                    callableStatement.execute();
                    return callableStatement.getInt(1);
                }
            }
        );

        if (sorted < 0) {
            throw new RuntimeException("Error Renumbering the Point of Diversions.");
        }

        return sorted;
    }
}
