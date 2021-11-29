package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.MailingJobsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.JobPartiesPageDto;
import gov.mt.wris.dtos.JobPartiesSortColumn;
import gov.mt.wris.dtos.JobPartyByOfficeCreationDto;
import gov.mt.wris.dtos.JobPartyCreationDto;
import gov.mt.wris.dtos.JobWaterRightCreationDto;
import gov.mt.wris.dtos.JobWaterRightPageDto;
import gov.mt.wris.dtos.JobWaterRightSortColumn;
import gov.mt.wris.dtos.MailingJobCreationDto;
import gov.mt.wris.dtos.MailingJobDto;
import gov.mt.wris.dtos.MailingJobSortColumn;
import gov.mt.wris.dtos.MailingJobUpdateDto;
import gov.mt.wris.dtos.MailingJobsPageDto;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.OfficeContactPageDto;
import gov.mt.wris.dtos.OfficeContactSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.services.MailingJobPartyService;
import gov.mt.wris.services.MailingJobService;
import gov.mt.wris.services.MailingJobWaterRightService;

@Controller
public class MailingJobsController implements MailingJobsApiDelegate {
    public static Logger LOGGER = LoggerFactory.getLogger(MailingJobsController.class);

    @Autowired
    private MailingJobService mailingJobService;

    @Autowired
    private MailingJobWaterRightService waterRightService;

    @Autowired
    private MailingJobPartyService partyService;

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TYPE_TABLE)
    })
    public ResponseEntity<MailingJobsPageDto> searchMailingJobs(Integer pageNumber,
        Integer pageSize,
        MailingJobSortColumn sortColumn,
        DescSortDirection sortDirection,
        String mailingJobNumber,
        String mailingJobHeader,
        String applicationId
    ) {
        LOGGER.info("Searching for Mailing Jobs");

        MailingJobsPageDto dto = mailingJobService.searchMailingJobs(pageNumber, pageSize, sortColumn, sortDirection, mailingJobNumber, mailingJobHeader, applicationId);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.MAILING_JOB_TABLE)
    })
    public ResponseEntity<MailingJobDto> createMailingJob(MailingJobCreationDto creationDto) {
        LOGGER.info("Creating a Mailing Job");

        MailingJobDto dto = mailingJobService.createMailingJob(creationDto);

        return new ResponseEntity<>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TYPE_TABLE)
    })
    public ResponseEntity<MailingJobDto> getMailingJob(Long mailingJobId) {
        LOGGER.info("Getting a Mailing Job");

        MailingJobDto dto = mailingJobService.getMailingJob(mailingJobId);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.MAILING_JOB_TABLE)
    })
    public ResponseEntity<Void> generateMailingJob(Long mailingJobId) {
        LOGGER.info("Generating a Mailing Job");

        mailingJobService.generateMailingJob(mailingJobId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.MAILING_JOB_TABLE)
    })
    public ResponseEntity<MailingJobDto> editMailingJob(Long mailingJobId, MailingJobUpdateDto updateDto) {
        LOGGER.info("Updating a Mailing Job");

        MailingJobDto dto = mailingJobService.updateMailingJob(mailingJobId, updateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.MAILING_JOB_TABLE)
    })
    public ResponseEntity<Void> deleteMailingJob(Long mailingJobId) {
        LOGGER.info("Deleting a Mailing Job");

        mailingJobService.deleteMailingJob(mailingJobId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_WATER_RIGHT_XREF_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE)
    })
    public ResponseEntity<JobWaterRightPageDto> getMailingJobWaterRights(Long mailingJobId,
        Integer pageNumber,
        Integer pageSize,
        JobWaterRightSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting the Water Rights attached to a Mailing Job");

        JobWaterRightPageDto dto = waterRightService.getJobWaterRights(mailingJobId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_WATER_RIGHT_XREF_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.MAILING_JOB_WATER_RIGHT_XREF_TABLE)
    })
    public ResponseEntity<Void> addJobWaterRight(Long mailingJobId, JobWaterRightCreationDto creationDto) {
        LOGGER.info("Adding a Water Right to a Mailing Job");

        waterRightService.addJobWaterRight(mailingJobId, creationDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_WATER_RIGHT_XREF_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.MAILING_JOB_WATER_RIGHT_XREF_TABLE)
    })
    public ResponseEntity<Void> removeJobWaterRight(Long mailingJobId, Long waterRightId) {
        LOGGER.info("Removing a Water Right from a Mailing Job");

        waterRightService.removeJobWaterRight(mailingJobId, waterRightId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_WATER_RIGHT_XREF_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.MAILING_JOB_WATER_RIGHT_XREF_TABLE)
    })
    public ResponseEntity<Message> importJobWaterRights(Long mailingJobId, MultipartFile file) {
        LOGGER.info("Importing Water Rights");

        Message message = waterRightService.importJobWaterRights(mailingJobId, file);

        return new ResponseEntity<>(message, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_XREF_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TYPES_TABLE)
    })
    public ResponseEntity<JobPartiesPageDto> getMailingJobInterestedParties(Long mailingJobId,
        Integer pageNumber,
        Integer pageSize,
        JobPartiesSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting the Interested Parties of a Mailing Job");

        JobPartiesPageDto dto = partyService.getMailingJobParties(mailingJobId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_XREF_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.MAILING_JOB_XREF_TABLE),
    })
    public ResponseEntity<Void> addJobParty(Long mailingJobId, JobPartyCreationDto creationDto) {
        LOGGER.info("Adding an Interested Party to a Mailing Job");

        partyService.addInterestedParty(mailingJobId, creationDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.OFFICE_CONTACT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_XREF_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.MAILING_JOB_XREF_TABLE)
    })
    public ResponseEntity<Void> addJobPartiesByOffice(Long mailingJobId, Long officeId, JobPartyByOfficeCreationDto creationDto) {
        LOGGER.info("Adding Interested Parties by Office to a Mailing Job");

        partyService.addInterestedPartyByOffice(mailingJobId, officeId, creationDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_XREF_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.MAILING_JOB_XREF_TABLE)
    })
    public ResponseEntity<Void> removeJobParty(Long mailingJobId, Long contactId) {
        LOGGER.info("Removing an Interested Party from a Mailing Job");

        partyService.removeInterestedParty(mailingJobId, contactId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.OFFICE_CONTACT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TYPES_TABLE)
    })
    public ResponseEntity<OfficeContactPageDto> getOfficeContactsNotInMailingJob(Long mailingJobId,
        Long officeId,
        Integer pageNumber,
        Integer pageSize,
        OfficeContactSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting the Contacts of an Office");

        OfficeContactPageDto dto = partyService.getOfficeContacts(mailingJobId, officeId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }
}