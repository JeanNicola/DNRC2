package gov.mt.wris.services;

import org.springframework.web.multipart.MultipartFile;

import gov.mt.wris.dtos.JobWaterRightCreationDto;
import gov.mt.wris.dtos.JobWaterRightPageDto;
import gov.mt.wris.dtos.JobWaterRightSortColumn;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.SortDirection;

public interface MailingJobWaterRightService {
    public JobWaterRightPageDto getJobWaterRights(Long mailingJobId,
        int pagenumber,
        int pagesize,
        JobWaterRightSortColumn sortColumn,
        SortDirection sortDirection);

    public void addJobWaterRight(Long mailingJobId, JobWaterRightCreationDto creationDto);

    public void removeJobWaterRight(Long mailingJobId, Long waterRightId);

    public Message importJobWaterRights(Long mailingJobId, MultipartFile file);
}
