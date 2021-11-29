package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.CorrectComplete;

/**
 * @author Cesar.Zamorano
 *
 */
@Repository
public interface CorrectCompletesRepository extends CrudRepository<CorrectComplete, BigDecimal> {
	
	@Query( value = "SELECT cc\n"
			+ "FROM CorrectComplete cc\n"
			+ "JOIN FETCH cc.correctCompleteType t\n"
			+ "WHERE cc.objectionId = :objectionId \n",
			countQuery = "SELECT COUNT(cc) FROM CorrectComplete cc WHERE cc.objectionId = :objectionId")
	public Page<CorrectComplete> getCorrectCompletes(Pageable pageable, @Param("objectionId") BigDecimal objectionId);

}
