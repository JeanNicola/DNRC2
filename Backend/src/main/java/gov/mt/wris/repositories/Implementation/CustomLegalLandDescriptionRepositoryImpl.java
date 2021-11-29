package gov.mt.wris.repositories.Implementation;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.mt.wris.repositories.CustomLegalLandDescriptionRepository;

public class CustomLegalLandDescriptionRepositoryImpl implements CustomLegalLandDescriptionRepository {
    private static Logger LOGGER = LoggerFactory.getLogger(CustomLegalLandDescriptionRepository.class);

    @PersistenceContext
    EntityManager manager;

    public Long validateLegalLandDescription(
        String description320,
        String description160,
        String description80,
        String description40,
        Long governmentLot,
        Long township,
        String townshipDirection,
        Long range,
        String rangeDirection,
        Long section,
        Long countyId
    ) {
        LOGGER.info("Validating a Legal Land Description");

        Session session = manager.unwrap(Session.class);

        Long ac = session.doReturningWork(
            connection -> {
                try (CallableStatement callableStatement = connection.prepareCall("{? = call WRD_COMMON_FUNCTIONS.VALIDATE_LL(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
                    callableStatement.registerOutParameter(1, Types.BIGINT);
                    callableStatement.setString(2, description320);
                    callableStatement.setString(3, description160);
                    callableStatement.setString(4, description80);
                    callableStatement.setString(5, description40);
                    if (governmentLot != null) {
                        callableStatement.setLong(6, governmentLot);
                    } else {
                        callableStatement.setNull(6, Types.BIGINT);
                    }
                    if (township != null) {
                        callableStatement.setLong(7, township);
                    } else {
                        callableStatement.setNull(7, Types.BIGINT);
                    }
                    callableStatement.setString(8, townshipDirection);
                    if (range != null) {
                        callableStatement.setLong(9, range);
                    } else {
                        callableStatement.setNull(9, Types.BIGINT);
                    }
                    callableStatement.setString(10, rangeDirection);
                    if (section != null) {
                        callableStatement.setLong(11, section);
                    } else {
                        callableStatement.setNull(11, Types.BIGINT);
                    }
                    if(countyId != null) {
                        callableStatement.setLong(12, countyId);
                    } else {
                        callableStatement.setNull(12, Types.BIGINT);
                    }
                    callableStatement.execute();
                    return callableStatement.getLong(1);
                }
            }
        );

        return ac;
    }
}
