package gov.mt.wris.services;

import java.math.BigDecimal;

import gov.mt.wris.dtos.CategoriesPageDto;
import gov.mt.wris.dtos.CategorySortColumn;
import gov.mt.wris.dtos.SortDirection;

/**
 * @author Cesar.Zamorano
 *
 */
public interface CategoriesService {

	/**
	 * @param applicationId
	 * @param objectionId
	 * @param pageNumber
	 * @param pageSize
	 * @param sortColumn
	 * @param sortDirection
	 * @return
	 */
	public CategoriesPageDto getCategories(BigDecimal applicationId, BigDecimal objectionId, Integer pageNumber,
			Integer pageSize, CategorySortColumn sortColumn, SortDirection sortDirection);

}
