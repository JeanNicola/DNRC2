package gov.mt.wris.repositories;

import gov.mt.wris.models.ElementType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElementTypeRepository extends JpaRepository<ElementType, String> {
}
