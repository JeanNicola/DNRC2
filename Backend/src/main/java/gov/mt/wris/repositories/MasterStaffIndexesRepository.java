package gov.mt.wris.repositories;

import gov.mt.wris.models.MasterStaffIndexes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Cesar.Zamorano
 *
 */
@Repository
public interface MasterStaffIndexesRepository extends JpaRepository<MasterStaffIndexes, BigDecimal>{

	/**
	 * Gets user full name from database.
	 * 
	 * @param directoryUser
	 * @return
	 */
	public Optional<MasterStaffIndexes> findByDirectoryUserAndEndDateNull(String directoryUser);

	/*
	 * Get the database environment the user is running in
	 */
	@Query(value = "SELECT GLOBAL_NAME\n" +
								"FROM GLOBAL_NAME",
								nativeQuery = true
	)
	public String getDatabaseEnvironment();

	public List<MasterStaffIndexes> findAllByEndDateIsNullOrderByLastNameAscFirstNameAsc();

	@Query(value = "SELECT COUNT(*)\n" +
			"FROM USER_ROLE_PRIVS\n" +
			"WHERE granted_role IN :roles\n",
			nativeQuery = true)
	public int hasRoles(@Param("roles") List<String> roles);

    @Query(value = " SELECT msi \n" +
            " FROM MasterStaffIndexes msi \n" +
            " WHERE msi.lastName LIKE :lastName \n" +
            " AND ((:firstName IS NULL) OR (msi.firstName LIKE :firstName))",
            countQuery = "SELECT COUNT(msi) FROM MasterStaffIndexes msi WHERE msi.lastName LIKE :lastName AND ((:firstName IS NULL) OR (msi.firstName LIKE :firstName))"
    )
	public Page<MasterStaffIndexes> searchAllByName(Pageable pageable, @Param("lastName") String lastName, @Param("firstName") String firstName);

	public List<MasterStaffIndexes> findMasterStaffIndexesByDistrictCourtAndPositionCodeOrderByLastNameAscFirstNameAsc(Integer districtCourt, String positionCode);

}
