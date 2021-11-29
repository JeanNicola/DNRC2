package gov.mt.wris.services;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionObjectionsElementsPageDto;
import gov.mt.wris.dtos.WaterRightVersionObjectionsElementsSortColumn;

import java.math.BigDecimal;

public interface ElementObjectionService {

   public WaterRightVersionObjectionsElementsPageDto getWaterRightVersionObjectionElements(Integer pageNumber, Integer pageSize, WaterRightVersionObjectionsElementsSortColumn sortColumn, SortDirection sortDirection, BigDecimal waterRightId, BigDecimal versionId, BigDecimal objectionId);

}
