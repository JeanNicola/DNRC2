package gov.mt.wris.services;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightOwnerSortColumn;
import gov.mt.wris.dtos.WaterRightOwnershipSortColumn;
import gov.mt.wris.dtos.WaterRightOwnershipUpdatePageDto;

public interface WaterRightOwnershipUpdateService {
    public WaterRightOwnershipUpdatePageDto getWaterRightOwnershipUpdates(int pagenumber,
        int pagesize,
        WaterRightOwnershipSortColumn sortDTOColumn,
        SortDirection sortDirection,
        Long waterRightId);
}
