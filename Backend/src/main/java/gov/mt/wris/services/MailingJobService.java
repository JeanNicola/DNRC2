package gov.mt.wris.services;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.MailingJobCreationDto;
import gov.mt.wris.dtos.MailingJobDto;
import gov.mt.wris.dtos.MailingJobSortColumn;
import gov.mt.wris.dtos.MailingJobUpdateDto;
import gov.mt.wris.dtos.MailingJobsPageDto;

public interface MailingJobService {
    public MailingJobsPageDto searchMailingJobs(int pagenumber,
        int pagesize,
        MailingJobSortColumn sortColumn,
        DescSortDirection sortDirection,
        String mailingJobNumber,
        String mailingJobHeader,
        String applicationId);

    public MailingJobDto createMailingJob(MailingJobCreationDto creationDto);

    public MailingJobDto getMailingJob(Long mailingJobId);

    public void generateMailingJob(Long mailingJobId);

    public MailingJobDto updateMailingJob(Long mailingJobId, MailingJobUpdateDto updateDto);

    public void deleteMailingJob(Long mailingJobId);
}
