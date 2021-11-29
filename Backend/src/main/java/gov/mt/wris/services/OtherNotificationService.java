package gov.mt.wris.services;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.OtherNotificationPageDto;
import gov.mt.wris.dtos.OtherNotificationSortColumn;
import gov.mt.wris.dtos.SortDirection;

public interface OtherNotificationService {
    public OtherNotificationPageDto findOtherNotificationsByAppIdAndByMailingJobId(
            String applicationId,
            String mailingJobId,
            Integer pageNumber,
            Integer pageSize,
            OtherNotificationSortColumn sortColumn,
            SortDirection sortDirection
    );
}
