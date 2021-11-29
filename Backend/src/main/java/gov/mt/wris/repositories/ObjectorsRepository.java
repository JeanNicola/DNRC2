package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.Objector;

/**
 * Base repository for Objectors Table.
 *
 * @author Cesar.Zamorano
 */
@Repository
public interface ObjectorsRepository extends JpaRepository<Objector, BigDecimal> {

	/**
	 * @param pageable
	 * @param applicationId
	 * @return
	 */
	@Query( value = "SELECT o\n"
			+ "FROM Objector o\n"
			+ "JOIN FETCH o.customer c\n"
			+ "WHERE o.objectionId = :objectionId \n",
			countQuery = "SELECT COUNT(o) FROM Objector o WHERE o.objectionId = :objectionId")
	public Page<Objector> getObjectors(Pageable pageable, @Param("objectionId") BigDecimal objectionId);

	Optional<Objector> findObjectorsByObjectionIdAndCustomerId(BigDecimal objectionId, BigDecimal customerId);
}
