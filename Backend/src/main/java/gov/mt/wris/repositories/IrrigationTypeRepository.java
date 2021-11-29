package gov.mt.wris.repositories;

import gov.mt.wris.models.IrrigationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IrrigationTypeRepository extends JpaRepository<IrrigationType, String> {
}
