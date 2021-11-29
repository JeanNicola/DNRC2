package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.mt.wris.models.RoleType;

/**
 * Base repository for RoleType Table.
 *
 * @author Cesar.Zamorano
 */
public interface RoleTypesRepository extends CrudRepository<RoleType, BigDecimal> {

	@Query(value = "SELECT r\n"+
					"FROM RoleType r\n" +
					"WHERE r.code IS NOT NULL\n" +
					"ORDER BY r.description")
	public List<RoleType> getRoleTypes();

}
