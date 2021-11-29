package gov.mt.wris.services;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightNotificationPageDto;
import gov.mt.wris.dtos.WaterRightNotificationSortColumn;

public interface WaterRightNotificationService {
    public WaterRightNotificationPageDto findWaterRightsByMailingJobId(
            String applicationId,
            String mailingJobId,
            Integer pageNumber,
            Integer pageSize,
            WaterRightNotificationSortColumn sortColumn,
            SortDirection sortDirection
    );
}
