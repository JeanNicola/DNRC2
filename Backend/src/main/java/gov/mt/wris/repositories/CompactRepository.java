package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gov.mt.wris.models.Compact;

public interface CompactRepository extends JpaRepository<Compact, BigDecimal>, CustomCompactRepository {

}
