package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.mt.wris.models.County;

/**
 * Base repository for Counties Table.
 *
 * @author Cesar.Zamorano
 */
public interface CountiesRepository extends JpaRepository<County, BigDecimal> {

	@Query(value = "SELECT c\n"+
									"FROM County c\n" +
									"WHERE c.stateCountyNumber IS NOT NULL\n" +
									"ORDER BY c.name")
	public List<County> getCountiesOfMontana();

	@Query(value = "SELECT c\n" +
		"FROM County c\n" +
		"WHERE c.stateCode <> 'XX'\n" +
		"order by c.name")
	public List<County> findByOrderByName();

	public List<County> getCountiesByDistrictCourtOrderByName(Integer districtCourt);

}
