package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.SubdivisionCode;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

/**
 * Base Repository for SubdivisionCodes table.
 *
 * @author Cesar.Zamorano
 */
public interface SubdivisionCodesRepository
		extends CrudRepository<SubdivisionCode, String>, CustomSubdivisionCodesRepository {

	/**
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
