package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.SystemVariables;

public interface SystemVariableRepository extends JpaRepository<SystemVariables, BigDecimal> {
    @Query("SELECT s.value\n" +
            "FROM SystemVariables s\n" +
            "WHERE s.name = '" + Constants.SYSTEM_ENFORCEMENT_EMAILS + "'")
    List<String> findEmails();
}
