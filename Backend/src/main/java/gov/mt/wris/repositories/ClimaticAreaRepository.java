package gov.mt.wris.repositories;

import gov.mt.wris.models.ClimaticArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClimaticAreaRepository extends JpaRepository<ClimaticArea, String> {
}
