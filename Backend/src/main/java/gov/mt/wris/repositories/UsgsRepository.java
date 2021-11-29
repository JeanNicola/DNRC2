package gov.mt.wris.repositories;

import gov.mt.wris.models.Usgs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface UsgsRepository extends JpaRepository<Usgs, BigDecimal>, CustomUsgsRepository {

}
