package gov.mt.wris.repositories;

import gov.mt.wris.models.EnforcementArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnforcementAreaRepository extends JpaRepository<EnforcementArea, String>, CustomEnforcementAreaRepository {

    public List<EnforcementArea> findAllByOrderById();

    Optional<EnforcementArea> getEnforcementAreaById(String id);

}
