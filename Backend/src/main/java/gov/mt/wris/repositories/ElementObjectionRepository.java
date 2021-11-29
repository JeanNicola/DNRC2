package gov.mt.wris.repositories;

import gov.mt.wris.models.ElementObjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Repository
public interface ElementObjectionRepository extends JpaRepository<ElementObjection, BigDecimal> {

    public Page<ElementObjection> findElementObjectionByObjectionId(Pageable pageable, BigDecimal objectionId);

}
