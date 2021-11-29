package gov.mt.wris.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.CaseStatus;

/**
 * Custom repository for pageable Case Status searches.
 *
 * @author Cesar.Zamorano
 */
@Repository
public interface CustomCaseStatusRepository {

	/**
	 * Creates a SELECT command to Case Status Table, with a WHERE clause containing
	 * optional values and optional paging configuration. Can return 0, a page of
	 * elements or all of them, sorted by column and/or direction.
	 * 
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
