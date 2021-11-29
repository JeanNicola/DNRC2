package gov.mt.wris.services;

import java.util.Optional;

import gov.mt.wris.dtos.CaseStatusDto;
import gov.mt.wris.dtos.CaseStatusPageDto;
import gov.mt.wris.dtos.CaseStatusSortColumn;
import gov.mt.wris.dtos.SortDirection;

/**
 * The service which contains any type of operation against Case Status table.
 * 
 * @author Cesar.Zamorano
 *
 */
public interface CaseStatusService {

	/**
	 * Obtains a Case Status element, by a code.
	 * 
	 * @param code
	 * @return
	 */
	Optional<CaseStatusDto> getCaseStatus(String code);

	/**
	 * Searches with filters, and can return all results or a configurable page of
	 * Case Status elements, sorted by a column.
	 * 
	 * @param pagenumber
	 * @param pagesize
	 * @param sortColumn
	 * @param sortDirection
	 * @param code
	 * @param description
	 * @return
	 */
	CaseStatusPageDto getCaseStatuses(int pagenumber, int pagesize, CaseStatusSortColumn sortColumn,
			SortDirection sortDirection, String code, String description);

	/**
	 * Creates a new Case Status element in the table.
	 * 
	 * @param caseDTO
	 * @return
	 */
	CaseStatusDto createCaseStatus(CaseStatusDto caseDTO);

	/**
	 * Deletes a Case Status element from the table, with the code received.
	 * 
	 * @param code
	 */
	void deleteCaseStatus(String code);

	/**
	 * Updates a Case Status element from the table, with the original code and a
	 * new element received.
	 * 
	 * @param caseDto
	 * @param code
	 * @return
	 */
	Optional<CaseStatusDto> replaceCaseStatus(CaseStatusDto caseDto, String code);

	/**
	 * Transforms all String values to upperCase.
	 * 
	 * @param Dto
	 * @return
	 */
	public CaseStatusDto toUpperCase(CaseStatusDto Dto);
}
