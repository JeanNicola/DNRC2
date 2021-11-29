package gov.mt.wris.services;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.ApplicationMailingJobPageDto;
import gov.mt.wris.dtos.ApplicationMailingJobSortColumn;
import gov.mt.wris.dtos.SortDirection;

public interface NoticeService {
    public ApplicationMailingJobPageDto findNotices(
            String applicationId,
            Integer pageNumber,
            Integer pageSize,
            ApplicationMailingJobSortColumn sortColumn,
            DescSortDirection sortDirection
    );
}
