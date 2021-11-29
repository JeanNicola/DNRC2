package gov.mt.wris.services;

import java.math.BigDecimal;
import java.util.Optional;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubdivisionCodeDto;
import gov.mt.wris.dtos.SubdivisionCodePageDto;
import gov.mt.wris.dtos.SubdivisionCodesSortColumn;

/**
 * The service which contains any type of operation against SubdivisionCodes
 * table.
 *
 * @author Cesar.Zamorano
 */
public interface SubdivisionCodesService {

	/**
	 * Obtains a SubdivisionCode element, by a code.
	 * 
	 * @param code
	 * @return
	 */
	Optional<SubdivisionCodeDto> getSubdivisionCode(String code);

	/**
	 * Searches with filters, and can return all results or a configurable page of
	 * SubdivisionCode elements, sorted by a column.
	 * 
	 * @param pagenumber
	 * @param pagesize
	 * @param sortColumn
	 * @param sortDirection
	 * @param code
	 * @param countyName
	 * @param dnrcName
	 * @param dorName
	 * @return
	 */
	SubdivisionCodePageDto getSubdivisionCodes(int pagenumber, int pagesize, SubdivisionCodesSortColumn sortColumn,
			SortDirection sortDirection, String code, BigDecimal countyId, String countyName, String dnrcName,
			String dorName);

	/**
	 * Creates a new SubdivisionCode element in the table.
	 * 
	 * @param subdivisionCodeDTO
	 * @return
	 */
	SubdivisionCodeDto createSubdivisionCode(SubdivisionCodeDto subdivisionCodeDTO);

	/**
	 * Deletes a SubdivisionCode element from the table, with the code received.
	 * 
	 * @param code
	 */
	void deleteSubdivisionCode(String code);

	/**
	 * Updates a SubdivisionCode element from the table, with the original code and
	 * a new element received.
	 * 
	 * @param subdivisionCodeDTO
	 * @param code
	 * @return
	 */
	Optional<SubdivisionCodeDto> replaceSubdivisionCode(SubdivisionCodeDto subdivisionCodeDTO, String code);

	/**
	 * Transforms all String values to upperCase.
	 * 
	 * @param subCodeDto
	 * @return
	 */
	SubdivisionCodeDto toUpperCase(SubdivisionCodeDto subCodeDto);

}
