package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.ApplicationsApiDelegate;
import gov.mt.wris.constants.Constants;
import java.math.BigDecimal;
import java.util.Optional;
import javax.validation.ValidationException;
import gov.mt.wris.dtos.*;
import gov.mt.wris.exceptions.DataConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.ApplicationsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.services.ApplicantService;
import gov.mt.wris.services.ApplicationAutoCompleteService;
import gov.mt.wris.services.ApplicationService;
import gov.mt.wris.services.CategoriesService;
import gov.mt.wris.services.EventService;
import gov.mt.wris.services.NoticeService;
import gov.mt.wris.services.ObjectionsService;
import gov.mt.wris.services.ObjectorsService;
import gov.mt.wris.services.OtherNotificationService;
import gov.mt.wris.services.PaymentService;
import gov.mt.wris.services.RepresentativeService;
import gov.mt.wris.services.WaterRightNotificationService;
import gov.mt.wris.services.WaterRightVersionService;


@Controller
public class ApplicationsController implements ApplicationsApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationsController.class);

    @Autowired
    private ApplicationService appService;

    @Autowired
    private ApplicantService applicantService;

    @Autowired
    private RepresentativeService repreService;

    @Autowired
    private EventService eventService;

    // @Autowired
    // private ChangeService changeService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ObjectionsService objectionsService;

    @Autowired
    private ObjectorsService objectorsService;

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private ApplicationAutoCompleteService autoCompleteService;

    @Autowired
    private WaterRightNotificationService waterRightNotificationService;

    @Autowired
    private OtherNotificationService otherNotificationService;

    @Autowired
    private WaterRightVersionService waterRightService;

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TYPE_TABLE)
    })
    public ResponseEntity<RelatedApplicationPageDto> getAllRelatedApplications(String applicationId,
                                                                               Integer pageNumber,
                                                                               Integer pageSize,
                                                                               RelatedApplicationSortColumn sortColumn,
                                                                               SortDirection sortDirection) {

        RelatedApplicationPageDto dto = appService.findRelatedApplications(
                applicationId,
                pageNumber,
                pageSize,
                sortColumn,
                sortDirection
        );

        return ResponseEntity.ok(dto);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REPRESENTATIVE_TABLE)
    })
    public ResponseEntity<ApplicationSearchPageDto> searchApplications(Integer pageNumber,
                                                                       Integer pageSize,
                                                                       ApplicationSortColumn sortColumn,
                                                                       DescSortDirection sortDirection,
                                                                       String basin,
                                                                       String applicationId,
                                                                       String applicationTypeCode) {
        LOGGER.info("Search for Applications");

        ApplicationSearchPageDto appPage = appService.getApplications(pageNumber, pageSize, sortColumn, sortDirection,
                basin, applicationId, applicationTypeCode);
        return ResponseEntity.ok(appPage);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REPRESENTATIVE_TABLE)
    })
    public ResponseEntity<ApplicationOwnerSearchPageDto> searchApplicationsByOwner(Integer pageNumber,
                                                                                 Integer pageSize,
                                                                                 ApplicationOwnerSortColumn ownerSortColumn,
                                                                                 DescSortDirection sortDirection,
                                                                                 String basin,
                                                                                 String applicationId,
                                                                                 String applicationTypeCode,
                                                                                 String ownerContactId,
                                                                                 String ownerLastName,
                                                                                 String ownerFirstName,
                                                                                 String repContactId,
                                                                                 String repLastName,
                                                                                 String repFirstName) {
        LOGGER.info("Search for Applications by owner and representatives");

        // Validate if onwer lastname is used, owner firstname is also included
        if (ownerFirstName != null && ownerLastName == null && ownerContactId == null) {
            throw new ValidationException("OwnerLastName filter must be present when using OwnerFirstName filter");
        }
        if (repFirstName != null && repLastName == null && repContactId == null) {
            throw new ValidationException("repLastName filter must be present when using repFirstName filter");
        }

        ApplicationOwnerSearchPageDto appPage = appService.getApplicationsByOwners(pageNumber, pageSize, ownerSortColumn, sortDirection,
                basin, applicationId, applicationTypeCode,
                ownerContactId, ownerLastName, ownerFirstName, repContactId, repLastName, repFirstName);
        return ResponseEntity.ok(appPage);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REPRESENTATIVE_TABLE)
    })
    public ResponseEntity<OwnerApplicationRepPageDto> getOwnersApplications(Long contactId,
                                                                            Integer pageNumber,
                                                                            Integer pageSize,
                                                                            OwnerApplicationSortColumn sortColumn,
                                                                            DescSortDirection sortDirection,
                                                                            String basin,
                                                                            String applicationId,
                                                                            String applicationTypeCode,
                                                                            String repContactId,
                                                                            String repLastName,
                                                                            String repFirstName) {
        LOGGER.info("Get the applications that belong to a particular owner");

        if (repFirstName != null && repLastName == null && repContactId == null){
            throw new ValidationException("repLastName filter must be present when using repFirstName filter");
        }

        OwnerApplicationRepPageDto appPage = appService.getOwnersApplications(pageNumber, pageSize, sortColumn, sortDirection, contactId, basin, applicationId, applicationTypeCode, repContactId, repLastName, repFirstName);

        return ResponseEntity.ok(appPage);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REPRESENTATIVE_TABLE)
    })
    public ResponseEntity<ApplicationRepSearchPageDto> searchApplicationsByRepresentative(Integer pageNumber,
                                                                                 Integer pageSize,
                                                                                 ApplicationRepSortColumn sortColumn,
                                                                                 DescSortDirection sortDirection,
                                                                                 String basin,
                                                                                 String applicationId,
                                                                                 String applicationTypeCode,
                                                                                 String repContactId,
                                                                                 String repLastName,
                                                                                 String repFirstName) {
        LOGGER.info("Search for Applications by representatives");

        // Validate if onwer lastname is used, owner firstname is also included
        if (repFirstName != null && repLastName == null && repContactId == null) {
            throw new ValidationException("repLastName filter must be present when using repFirstName filter");
        }

        ApplicationRepSearchPageDto appPage = appService.getApplicationsByReps(pageNumber, pageSize, sortColumn, sortDirection,
                basin, applicationId, applicationTypeCode,
                repContactId, repLastName, repFirstName);
        return ResponseEntity.ok(appPage);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REPRESENTATIVE_TABLE)
    })
    public ResponseEntity<RepApplicationOwnerPageDto> getRepresentativesApplications(Long repContactID,
                                                                            Integer pageNumber,
                                                                            Integer pageSize,
                                                                            RepApplicationSortColumn sortColumn,
                                                                            DescSortDirection sortDirection,
                                                                            String basin,
                                                                            String applicationId,
                                                                            String applicationTypeCode) {
        LOGGER.info("Get the applications that belong to a particular representatives");

        RepApplicationOwnerPageDto appPage = appService.getRepsApplications(pageNumber, pageSize, sortColumn, sortDirection, repContactID, basin, applicationId, applicationTypeCode);

        return ResponseEntity.ok(appPage);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE)
    )
    public ResponseEntity<ApplicationDto> getApplication(Long id) {
        LOGGER.info("Get a specific application");
        ApplicationDto app = appService.getApplication(id);
        return ResponseEntity.ok(app);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.INSERT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.STAFF_APPL_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OFFICE_APPL_XREFS_TABLE),
    })
    public ResponseEntity<ApplicationDto> createApplication(ApplicationCreationDto newApplication) {
        LOGGER.info("Creating a new Application");
        ApplicationDto createdApplication = appService.createApplication(newApplication);
        return new ResponseEntity<ApplicationDto>(createdApplication, null, HttpStatus.CREATED);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.UPDATE, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE)
    })
    public ResponseEntity<ApplicationDto> changeApplication(Long id, ApplicationUpdateDto newApp) {
        LOGGER.info("Updating an Application");
        ApplicationDto updatedApplication = appService.updateApplication(id, newApp);

        return ResponseEntity.ok(updatedApplication);
    }

    /* APPLICANTS (OWNERS) endpoint */

    @Override
    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE) })
    public ResponseEntity<ApplicantsPageDto> getApplicants(Long applicationId, Integer pageNumber, Integer pageSize,
            ApplicantSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Getting a Page of Applicants");

        ApplicantsPageDto dto = applicantService.getApplicants(applicationId, pageNumber, pageSize, sortColumn,
                sortDirection);

        return ResponseEntity.ok(dto);

    }

    @Override
    @PermissionsNeeded({ @Permission(verb = Constants.UPDATE, table = Constants.OWNER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE) })
    public ResponseEntity<ApplicantDto> changeApplicant(Long applicationId, Long ownerId, ApplicantDto applicantDto) {
        LOGGER.info("Modifying an Applicant.");

        // convert everything to upper case
        applicantDto = applicantService.toUpperCase(applicantDto);

        if (applicantDto.getOwnerId() == null) {
            applicantDto.setOwnerId(ownerId);
        }

        // Validation of code
        if (!ownerId.equals(applicantDto.getOwnerId())) {
            throw new DataConflictException(
                    "Changing the Applicant ownerId isn't allowed. Delete the Applicant and create a new one.");
        }

        Optional<ApplicantDto> applicant = applicantService.changeApplicant(applicationId, ownerId, applicantDto);

        if (applicant.isPresent()) {
            if (applicant.get().getOwnerId().equals(applicantDto.getOwnerId())) {
                return ResponseEntity.ok(applicant.get());
            } else {
                return new ResponseEntity<ApplicantDto>(applicant.get(), null, HttpStatus.MOVED_PERMANENTLY);
            }
        } else {
            // this will be changed into an error thrown from the service, so we don't have
            // to worry about the optional business
            return new ResponseEntity<ApplicantDto>(applicant.get(), null, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @PermissionsNeeded({ @Permission(verb = Constants.DELETE, table = Constants.OWNER_TABLE) })
    public ResponseEntity<Void> deleteApplicant(Long applicationId, Long ownerId) {
        LOGGER.info("Deleting an Applicant.");

        // convert everything to upper case
        applicantService.deleteApplicant(applicationId, ownerId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @Override
    @PermissionsNeeded({ @Permission(verb = Constants.INSERT, table = Constants.OWNER_TABLE) })
    public ResponseEntity<ApplicantDto> createApplicant(Long applicationId, ApplicantDto applicantDto) {
        LOGGER.info("Creating a new Applicant.");

        ApplicantDto savedCode = applicantService.createApplicant(applicationId, applicantDto);
        return new ResponseEntity<ApplicantDto>(savedCode, null, HttpStatus.CREATED);

    }


    // Event endpoints

    /**
     * @param applicationId (required)
     * @param eventsDto     The new event (required)
     * @return
     */
    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.INSERT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE)
    })
    public ResponseEntity<EventsDto> createEvent(String applicationId,
                                                 EventsDto eventsDto) {
        EventsDto savedCode = null;
        eventsDto = eventService.toUpperCase(eventsDto);
        savedCode = eventService.createEvent(applicationId, eventsDto);
        return new ResponseEntity<EventsDto>(savedCode, null, HttpStatus.CREATED);
    }

    /**
     * @param applicationId (required)
     * @param eventId       (required)
     * @param eventsDto     The event (required)
     * @return
     */
    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.UPDATE, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE)
    })
    public ResponseEntity<EventsDto> changeEvent(String applicationId,
                                                 Long eventId,
                                                 EventsDto eventsDto) {

        eventsDto = eventService.toUpperCase(eventsDto);
        applicationId = applicationId.toUpperCase();

        if (eventsDto != null && eventsDto.getEventId() == null) {
            eventsDto.setEventId(eventId);
        }

        // Validation of code
        if (eventsDto != null && !eventId.equals(eventsDto.getEventId())) {
            throw new DataConflictException(
                    "Changing the event id isn't allowed. Delete the Event and create a new one.");
        }

        Optional<EventsDto> updatedEvent = eventService.changeEvent(applicationId, eventId, eventsDto);
        if (eventsDto != null && updatedEvent.isPresent()) {
            if (updatedEvent.get().getEventId().equals(eventsDto.getEventId())) {
                return ResponseEntity.ok(updatedEvent.get());
            } else {
                return new ResponseEntity<EventsDto>(updatedEvent.get(), null, HttpStatus.MOVED_PERMANENTLY);
            }
        } else {
            return new ResponseEntity<EventsDto>(updatedEvent.get(), null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @param applicationId (required)
     * @param eventId       (required)
     * @return
     */
    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.DELETE, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE)
    })
    public ResponseEntity<Void> deleteEvent(String applicationId, Long eventId) {
        applicationId = applicationId.toUpperCase();
        eventService.deleteEvent(applicationId, eventId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE)
    })
    public ResponseEntity<EventsPageDto> findAllEvents(
            String applicationId,
            Integer pageNumber,
            Integer pageSize,
            EventsSortColumn sortColumn,
            SortDirection sortDirection
    ) {

        EventsPageDto dto = eventService.findEventsByApplicationId(
                applicationId,
                pageNumber,
                pageSize,
                sortColumn,
                sortDirection);
        return ResponseEntity.ok(dto);
    }

    /* REPRESENTATIVES endpoint */

    public ResponseEntity<RepresentativeDto> changeRepresentative(
            Long applicationId,
            Long ownerId,
            Long customerId,
            Long representativeId,
            RepresentativeDto representativeDto) {

        LOGGER.info("Modifying a Representative.");

        representativeDto = repreService.toUpperCase(representativeDto);

        if (representativeDto.getRepresentativeId() == null) {
            representativeDto.setRepresentativeId(representativeId);
        }

        // Validation of code
        if (!representativeId.equals(representativeDto.getRepresentativeId())) {
            throw new DataConflictException(
                    "Changing the Representative id isn't allowed. Delete the Representative and create a new one.");
        }

        Optional<RepresentativeDto> repre = repreService.changeRepresentative(
                new BigDecimal(applicationId),
                new BigDecimal(ownerId),
                new BigDecimal(customerId),
                new BigDecimal(representativeId),
                representativeDto);

        if (repre.isPresent()) {
            if (repre.get().getRepresentativeId().equals(representativeDto.getRepresentativeId())) {
                return ResponseEntity.ok(repre.get());
            } else {
                return new ResponseEntity<RepresentativeDto>(repre.get(), null, HttpStatus.MOVED_PERMANENTLY);
            }
        } else {
            // this will be changed into an error thrown from the service, so we don't have
            // to worry about the optional business
            return new ResponseEntity<RepresentativeDto>(repre.get(), null, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<RepresentativeDto> createRepresentative(
            Long applicationId,
            Long ownerId,
            Long customerId,
            RepresentativeDto representativeDto) {

        LOGGER.info("Creating a new Representative.");

        representativeDto = repreService.toUpperCase(representativeDto);
        RepresentativeDto savedCode = repreService.createRepresentative(
                new BigDecimal(applicationId),
                new BigDecimal(ownerId),
                new BigDecimal(customerId),
                representativeDto);

        return new ResponseEntity<RepresentativeDto>(savedCode, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Void> deleteRepresentative(Long applicationId, Long ownerId, Long customerId, Long representativeId) {

        LOGGER.info("Deleting a Representative.");

        repreService.deleteRepresentative(
                new BigDecimal(applicationId),
                new BigDecimal(ownerId),
                new BigDecimal(customerId),
                new BigDecimal(representativeId)
        );

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<RepresentativesPageDto> getRepresentatives(Long applicationId, Long ownerId, Long customerId,
            Integer pageNumber, Integer pageSize, RepresentativeSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Getting a Page of Representatives");

        RepresentativesPageDto dto = repreService.getRepresentatives(new BigDecimal(applicationId),
                new BigDecimal(ownerId), new BigDecimal(customerId), pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

	public ResponseEntity<ChangeDto> findChangeByApplicationId(String applicationId) {
		ChangeDto dto = appService.getChange(applicationId);
		return ResponseEntity.ok(dto);
    }

    public ResponseEntity<ChangeDto> updateChange(String applicationId, ChangeDto changeDto) {

		ChangeDto updatedChange = appService.updateChange(applicationId, changeDto);
		return ResponseEntity.ok(updatedChange);
    }

    // Payments endpoints
    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE) })
    public ResponseEntity<PaymentsPageDto> getPayments(Long applicationId, Integer pageNumber, Integer pageSize,
            PaymentSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get a page of the Payments Tab");

        PaymentsPageDto paymentsDto = paymentService.getPaymentPage(pageNumber, pageSize, sortColumn, sortDirection,
                applicationId);

        return ResponseEntity.ok(paymentsDto);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.APPLICATION_TABLE) })
    public ResponseEntity<PaymentSummaryDto> updatePaymentSummary(Long applicationId, PaymentSummaryDto summaryDto) {
        LOGGER.info("Update the Payment Summary");

        summaryDto = paymentService.toUpperCase(summaryDto);

        PaymentSummaryDto dto = paymentService.updatePaymentSummary(applicationId, summaryDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.APPLICATION_TABLE) })
    public ResponseEntity<PaymentDto> createPayment(Long applicationId, PaymentDto paymentDto) {
        LOGGER.info("Add a new Payment");

        // convert everything to upper case
        paymentDto = paymentService.toUpperCase(paymentDto);

        PaymentDto newPayment = paymentService.createPayment(applicationId, paymentDto);

        return new ResponseEntity<PaymentDto>(newPayment, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.PAYMENT_TABLE) })
    public ResponseEntity<PaymentDto> updatePayment(Long applicationId, Long paymentId, PaymentDto paymentDto) {
        LOGGER.info("Update a Payment");

        // convert everything to upper case
        paymentDto = paymentService.toUpperCase(paymentDto);
        PaymentDto newPayment = paymentService.updatePayment(applicationId, paymentId, paymentDto);

        return ResponseEntity.ok(newPayment);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.DELETE, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.APPLICATION_TABLE) })
    public ResponseEntity<Void> deletePayment(Long applicationId, Long paymentId) {
        LOGGER.info("Delete a Payment");

        paymentService.deletePayment(applicationId, paymentId);

        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.OBJECTIONS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CASE_APPLICATION_XREF_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CASE_STATUS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE) })
    public ResponseEntity<ObjectionsPageDto> getObjections(Long applicationId, Integer pageNumber, Integer pageSize,
            ObjectionSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get a page of the Objections Tab");

        ObjectionsPageDto page = objectionsService.getObjections(pageNumber, pageSize, sortColumn, sortDirection,
                new BigDecimal(applicationId));

        return ResponseEntity.ok(page);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.OBJECTIONS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OBJECTORS_TABLE) })
    public ResponseEntity<ObjectorsPageDto> getObjectors(Long applicationId, Long objectionId, Integer pageNumber,
            Integer pageSize, ObjectorSortColumn sortColumn, DescSortDirection sortDirection) {
        LOGGER.info("Get a page of the Objectors Tab");

        ObjectorsPageDto page = objectorsService.getObjectors(new BigDecimal(applicationId),
                new BigDecimal(objectionId), pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(page);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.OBJECTORS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REPRESENTATIVE_TABLE)
    })
    public ResponseEntity<RepresentativesPageDto> getObjectorRepresentatives(Long applicationId,
        Long objectionId,
        Long customerId,
        Integer pageNumber,
        Integer pageSize,
        RepresentativeSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of representatives for an Objector");

        RepresentativesPageDto dto = repreService.getObjectorRepresentatives(applicationId, objectionId, customerId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.OBJECTIONS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CORRECT_TYPES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CORRECT_COMPLETES_TABLE) })
    public ResponseEntity<CategoriesPageDto> getCategories(Long applicationId, Long objectionId, Integer pageNumber,
            Integer pageSize, CategorySortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get a page of the Category Tab");

        CategoriesPageDto page = categoriesService.getCategories(new BigDecimal(applicationId),
                new BigDecimal(objectionId), pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(page);

    }

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_TABLE) })
    public ResponseEntity<ApplicationMailingJobPageDto> getNotices(
            String applicationId,
            Integer pageNumber,
            Integer pageSize,
            ApplicationMailingJobSortColumn sortColumn,
            DescSortDirection sortDirection) {

        ApplicationMailingJobPageDto page = noticeService.findNotices(
                applicationId,
                pageNumber,
                pageSize,
                sortColumn,
                sortDirection);

        return ResponseEntity.ok(page);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_WATER_RIGHT_XREF_TABLE) })
    public ResponseEntity<WaterRightNotificationPageDto> getWaterRightNotifications(
            String applicationId,
            String noticeId,
            Integer pageNumber,
            Integer pageSize,
            WaterRightNotificationSortColumn sortColumn,
            SortDirection sortDirection) {

        WaterRightNotificationPageDto page = waterRightNotificationService.findWaterRightsByMailingJobId(
                applicationId,
                noticeId,
                pageNumber,
                pageSize,
                sortColumn,
                sortDirection);
        return ResponseEntity.ok(page);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.MAILING_JOB_XREF_TABLE) })
    public ResponseEntity<OtherNotificationPageDto> getOtherNotifications(
            String applicationId,
            String noticeId,
            Integer pageNumber,
            Integer pageSize,
            OtherNotificationSortColumn sortColumn,
            SortDirection sortDirection) {


        OtherNotificationPageDto page = otherNotificationService.findOtherNotificationsByAppIdAndByMailingJobId(
                applicationId,
                noticeId,
                pageNumber,
                pageSize,
                sortColumn,
                sortDirection);
        return ResponseEntity.ok(page);

    }


    /***
     * Auto-complete Water Right
     * @param applicationId The Application Id (required)
     * @param body  (optional)
     * @return ApplicationAutoCompleteDto
     */
    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.EXECUTE, table = Constants.COMMON_FUNCTIONS)
    })
    public ResponseEntity<ApplicationAutoCompleteDto> autoComplete(Long applicationId, Object body) {

        LOGGER.info("Auto complete the transfer of application number to water right number for new water right");
        ApplicationAutoCompleteDto dto = autoCompleteService.autoComplete(applicationId);
        return ResponseEntity.ok(dto);

    }

    /*
        Water Right Version Tab methods
    */

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
    })
    public ResponseEntity<ApplicationWaterRightsPageDto> getWaterRights(Long applicationId,
        Integer pageNumber,
        Integer pageSize,
        ApplicationWaterRightSortColumn sortColumn,
        DescSortDirection sortDirection) {
        LOGGER.info("Getting a page of Water Rights");

        ApplicationWaterRightsPageDto waterRights = waterRightService.getWaterRights(pageNumber, pageSize, sortColumn, sortDirection, applicationId);

        return ResponseEntity.ok(waterRights);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE)
    )
    public ResponseEntity<ApplicationWaterRightsSummaryDto> getWaterRightsSummary(Long applicationId) {
        LOGGER.info("Getting the summary of water rights");

        ApplicationWaterRightsSummaryDto summary = appService.getWaterRightSummary(applicationId);

        return ResponseEntity.ok(summary);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.APPLICATION_TABLE),
    })
    public ResponseEntity<ApplicationWaterRightsSummaryDto> editWaterRightsSummary(Long applicationId, ApplicationWaterRightsSummaryDto updateDto) {
        LOGGER.info("Updating the summary of Water Rights");

        ApplicationWaterRightsSummaryDto summary = appService.editWaterRightSummaryDto(applicationId, updateDto);

        return ResponseEntity.ok(summary);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_VERSION_XREF_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
    })
    public ResponseEntity<ApplicationWaterRightDto> addWaterRight(Long applicationId, ApplicationWaterRightCreationDto dto) {
        LOGGER.info("Adding a Water Right to an Application");
        
        ApplicationWaterRightDto newWaterRight = waterRightService.addWaterRight(applicationId, dto);

        return new ResponseEntity<ApplicationWaterRightDto>(newWaterRight, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_VERSION_XREF_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
    })
    public ResponseEntity<ApplicationWaterRightDto> editWaterRight(Long applicationId, Long waterRightId, Long versionId, ApplicationWaterRightDto dto) {
        LOGGER.info("Adding a Water Right to an Application");
        
        ApplicationWaterRightDto editedWaterRight = waterRightService.editWaterRight(applicationId, waterRightId, versionId, dto);

        return ResponseEntity.ok(editedWaterRight);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_VERSION_XREF_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.APPLICATION_VERSION_XREF_TABLE)
    })
    public ResponseEntity<Void> deleteWaterRight(Long applicationId, Long waterRightId, Long versionId) {
        LOGGER.info("Adding a Water Right to an Application");
        
        waterRightService.deleteApplicationWaterRightVersion(applicationId, waterRightId, versionId);

        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE)
    })
    public ResponseEntity<ResponsibleOfficeDto> getResponsibleOffice(Long applicationId) {
        LOGGER.info("Getting the Application's responsible office");

        ResponsibleOfficeDto dto = appService.getResponsibleOffice(applicationId);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE)
    })
    public ResponseEntity<ResponsibleOfficeDto> editApplicationResponsibleOffice(Long applicationId, ResponsibleOfficeDto dto) {
        LOGGER.info("Getting the Application's responsible office");

        ResponsibleOfficeDto returnDto = appService.editResponsibleOffice(applicationId, dto);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE)
    })
    public ResponseEntity<ProcessorDto> getApplicationProcessor(Long applicationId) {
        LOGGER.info("Getting the Application's Processor");

        ProcessorDto dto = appService.getProcessor(applicationId);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE)
    })
    public ResponseEntity<ProcessorDto> editApplicationProcessor(Long applicationId, ProcessorDto dto) {
        LOGGER.info("Editing the Application's Processor");

        ProcessorDto returnDto = appService.editProcessor(applicationId, dto);

        return ResponseEntity.ok(returnDto);
    }


    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICE_APPL_XREFS_TABLE)
    })
    public ResponseEntity<OfficePageDto> getApplicationOffices(Long applicationId, Integer pageNumber, Integer pageSize, OfficeSortColumn sortColumn, DescSortDirection sortDirection) {
        LOGGER.info("Getting a page of Offices belonging to an application");

        OfficePageDto dto = appService.getApplicationsOffices(applicationId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.STAFF_APPL_XREFS_TABLE)
    })
    public ResponseEntity<StaffPageDto> getApplicationStaff(Long applicationId, Integer pageNumber, Integer pageSize, StaffSortColumn sortColumn, DescSortDirection sortDirection) {
        LOGGER.info("Getting a page of Offices belonging to an application");

        StaffPageDto dto = appService.getApplicationsStaff(applicationId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICE_APPL_XREFS_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.OFFICE_APPL_XREFS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<OfficeDto> addApplicationOffice(Long applicationId, OfficeCreationDto dto) {
        LOGGER.info("Adding a new office");

        OfficeDto returnDto = appService.addApplicationOffice(applicationId, dto);

        return new ResponseEntity<OfficeDto>(returnDto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICE_APPL_XREFS_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.OFFICE_APPL_XREFS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<OfficeDto> editApplicationOffice(Long applicationId, Long officeXrefId, OfficeDto dto) {
        LOGGER.info("Editing an office");

        OfficeDto returnDto = appService.editApplicationOffice(applicationId, officeXrefId, dto);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.OFFICE_APPL_XREFS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<Void> deleteApplicationOffice(Long applicationId, Long officeXrefId) {
        LOGGER.info("Deleting an office");

        appService.deleteApplicationOffice(applicationId, officeXrefId);

        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.STAFF_APPL_XREFS_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.STAFF_APPL_XREFS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<StaffDto> editApplicationStaff(Long applicationId, Long officeXrefId, StaffDto dto) {
        LOGGER.info("Editing a staff member");

        StaffDto returnDto = appService.editApplicationStaff(applicationId, officeXrefId, dto);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.STAFF_APPL_XREFS_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.STAFF_APPL_XREFS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<StaffDto> addApplicationStaff(Long applicationId, StaffCreationDto dto) {
        LOGGER.info("Adding a new staff member");

        StaffDto returnDto = appService.addApplicationStaff(applicationId, dto);

        return new ResponseEntity<StaffDto>(returnDto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.STAFF_APPL_XREFS_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.STAFF_APPL_XREFS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<Void> deleteApplicationStaff(Long applicationId, Long staffXrefId) {
        LOGGER.info("Removing a staff member");

        appService.deleteApplicationStaff(applicationId, staffXrefId);

        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TYPE_TABLE)
    })
    public ResponseEntity<EligibleApplicationsSearchPageDto> getEligibleApplications(Integer pageNumber,
                                                                                     Integer pageSize,
                                                                                     EligibleApplicationsSortColumn sortColumn,
                                                                                     SortDirection sortDirection,
                                                                                     String applicationId) {

        LOGGER.info("Get eligible Applications for Objections or Counter Objections");
        EligibleApplicationsSearchPageDto dto = objectionsService.getEligibleApplications(pageNumber, pageSize, sortColumn, sortDirection, applicationId);
        return ResponseEntity.ok(dto);

    }

}
