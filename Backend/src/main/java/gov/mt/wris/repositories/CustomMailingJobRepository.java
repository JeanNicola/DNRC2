package gov.mt.wris.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.MailingJobSortColumn;
import gov.mt.wris.models.MailingJob;

public interface CustomMailingJobRepository {
    public Page<MailingJob> searchMailingJobs(Pageable pageable,
        MailingJobSortColumn sortColumn,
        DescSortDirection sortDirection,
        String mailingJobNumber,
        String mailingJobHeader,
        String applicationId);
}
