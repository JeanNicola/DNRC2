package gov.mt.wris.repositories;

import gov.mt.wris.models.NotTheSame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface NotTheSamesRepository extends JpaRepository<NotTheSame, BigDecimal>, CustomNotTheSamesRepository {

}
