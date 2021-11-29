package gov.mt.wris.repositories;

import gov.mt.wris.models.PurposeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurposeTypeRepository extends JpaRepository<PurposeType, String> {
}
