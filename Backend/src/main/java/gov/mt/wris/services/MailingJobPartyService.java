package gov.mt.wris.services;

import gov.mt.wris.dtos.JobPartiesPageDto;
import gov.mt.wris.dtos.JobPartiesSortColumn;
import gov.mt.wris.dtos.JobPartyByOfficeCreationDto;
import gov.mt.wris.dtos.JobPartyCreationDto;
import gov.mt.wris.dtos.OfficeContactPageDto;
import gov.mt.wris.dtos.OfficeContactSortColumn;
import gov.mt.wris.dtos.SortDirection;

public interface MailingJobPartyService {
    public JobPartiesPageDto getMailingJobParties(Long mailingJobId,
        int pagenumber,
        int pagesize,
        JobPartiesSortColumn sortColumn,
        SortDirection sortDirection);

    public void addInterestedParty(Long mailingJobId, JobPartyCreationDto creationDto);

    public void addInterestedPartyByOffice(Long mailingJobId, Long officeId, JobPartyByOfficeCreationDto creationDto);

    public void removeInterestedParty(Long mailingJobId, Long contactId);

    public OfficeContactPageDto getOfficeContacts(Long mailingJobId,
        Long officeId,
        int pagenumber,
        int pagesize,
        OfficeContactSortColumn sortColumn,
        SortDirection sortDirection);
}
