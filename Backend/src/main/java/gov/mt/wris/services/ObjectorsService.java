package gov.mt.wris.services;

import java.math.BigDecimal;

import gov.mt.wris.dtos.ObjectionCreationDto;
import gov.mt.wris.dtos.ObjectionsSearchResultDto;
import gov.mt.wris.dtos.ObjectorSortColumn;
import gov.mt.wris.dtos.ObjectorsPageDto;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionObjectorsPageDto;
import gov.mt.wris.dtos.WaterRightVersionObjectorsSortColumn;

/**
 * @author Cesar.Zamorano
 *
 */
public interface ObjectorsService {

	/**
	 * @param applicationId
	 * @param objectionId
	 * @param pageNumber
	 * @param pageSize
	 * @param sortColumn
	 * @param sortDirection
	 * @return
	 */
	public ObjectorsPageDto getObjectors(BigDecimal applicationId, BigDecimal objectionId, Integer pageNumber, Integer pageSize,
			ObjectorSortColumn sortColumn, DescSortDirection sortDirection);

	public WaterRightVersionObjectorsPageDto getWaterRightVersionObjectors(BigDecimal waterRightId, BigDecimal versionId, BigDecimal objectionId, Integer pageNumber,
																		   Integer pageSize, WaterRightVersionObjectorsSortColumn sortColumn, SortDirection sortDirection);

}
