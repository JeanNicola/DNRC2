package gov.mt.wris.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.CaseStatus;

/**
 * Base Repository for Case Status table.
 *
 * @author Cesar.Zamorano
 */
public interface CaseStatusRepository extends CrudRepository<CaseStatus, String>, CustomCaseStatusRepository {

	/**
	 * @param pageable
	 * @param sortColumn
	 * @param sortDirection
	 * @param code
	 * @param description
	 * @return
	 */
	public Page<CaseStatus> getCaseStatuses(Pageable pageable, String sortColumn, SortDirection sortDirection,
			String code, String description);

}
