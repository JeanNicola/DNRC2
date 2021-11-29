package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.SubdivisionCode;

/**
 * Custom repository for pageable SubdivisionCodes searches.
 * 
 * @author Cesar.Zamorano
 *
 */
public interface CustomSubdivisionCodesRepository {

	/**
	 * Creates a SELECT command to SubdivisionCodes Table, with a WHERE clause
	 * containing optional values and optional paging configuration. Can return 0, a
	 * page of elements or all of them, sorted by column and/or direction.
	 * 
	 * @param pageable
	 * @param sortColumn
	 * @param sortDirection
	 * @param code
	 * @param countyName
	 * @param dnrcName
	 * @param dorName
	 * @return
	 */
	public Page<SubdivisionCode> getSubdivisionCodes(Pageable pageable, String sortColumn, SortDirection sortDirection,
			String code, BigDecimal countyId, String countyName, String dnrcName, String dorName);
}
