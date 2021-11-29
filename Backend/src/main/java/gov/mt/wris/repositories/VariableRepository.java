package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.mt.wris.models.Variable;

public interface VariableRepository extends JpaRepository<Variable, BigDecimal> {
    
}
