package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.repositories.MasterStaffIndexesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.EventsDto;
import gov.mt.wris.dtos.EventsPageDto;
import gov.mt.wris.dtos.EventsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.Event;
import gov.mt.wris.models.EventType;
import gov.mt.wris.repositories.ApplicationRepository;
import gov.mt.wris.repositories.EventRepository;
import gov.mt.wris.repositories.EventTypeRepository;
import gov.mt.wris.services.EventService;
import gov.mt.wris.services.PaymentService;

/**
 * Implementation of EventService
 *
 * @author Vannara Houth
 */
@Service
public class EventServiceImpl implements EventService {

    private static Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private EventRepository repo;

    @Autowired
    private EventTypeRepository eventTypeRepo;

    @Autowired
    private ApplicationRepository appRepo;

    @Autowired
    private PaymentService payService;

    @Autowired
    private MasterStaffIndexesRepository masterStaffIndexesRepository;

    /**
     * @param model
     * @return
     */
    private EventsDto getEventsDto(Event model) {
        EventsDto newDto = new EventsDto();
        newDto.setEventId(model.getEventId().longValue());
        newDto.setEvent(model.getEventTypeCode());
        newDto.setEventDesc(model.getEventType() != null ? model.getEventType().getDescription() : null);
        newDto.setComments(model.getEventComment());
        newDto.setDateTime(model.getEventDate());
        newDto.setModifiedBy(getModifiedFullName(model));
        newDto.setCreateBy(getCreatedFullName(model));
        newDto.setModifiedDate(model.getModifiedDate());
        newDto.setCreatedDate(model.getCreatedDate());
        newDto.setResponseDue(model.getResponseDueDate());
        return newDto;
    }

    String getCreatedFullName(Event model) {
        String fullName = "";
        if (model != null && model.getCreatedByName() != null) {
            fullName += (model.getCreatedByName().getFirstName() != null) ? model.getCreatedByName().getFirstName() + " " : "";
            fullName += (model.getCreatedByName().getMidInitial() != null) ? model.getCreatedByName().getMidInitial() + " " : "";
            fullName += (model.getCreatedByName().getLastName() != null) ? model.getCreatedByName().getLastName() + "" : "";
        }
        return fullName.length() != 0 ? fullName : null;
    }

    String getModifiedFullName(Event model) {
        String fullName = "";
        if (model != null && model.getModifiedByName() != null) {
            fullName += (model.getModifiedByName().getFirstName() != null) ? model.getModifiedByName().getFirstName() + " " : "";
            fullName += (model.getModifiedByName().getMidInitial() != null) ? model.getModifiedByName().getMidInitial() + " " : "";
            fullName += (model.getModifiedByName().getLastName() != null) ? model.getModifiedByName().getLastName() + "" : "";
        }
        return fullName.length() != 0 ? fullName : null;
    }

    /**
     * @param applicationId
     * @param eventsDto
     * @return
     */
    @Override
    @Transactional
    public EventsDto createEvent(String applicationId, EventsDto eventsDto) {
        LOGGER.info("Creating an event");
        
        Optional<Application> app = appRepo.findApplicationsWithType(new BigDecimal(applicationId));
        Event newEvent = null;

        if (!app.isPresent()) {
            throw new NotFoundException("The Application with Id " + applicationId + " was not found.");
        }

        if (!validateDateTime(eventsDto.getDateTime())) {
            throw new ValidationException("Please enter an event date between 1/1/1850 and 12/31/2100");
        }

        Optional<EventType> optEventType = eventTypeRepo.findById(eventsDto.getEvent());
        if (!optEventType.isPresent()) {
            throw new NotFoundException("Provided event has an unrecognized type code.");
        }

        // New code was added to hardcode the PHAM event to 180 days as that is what occurs in the original Oracle Forms code.
        // The new code to use the WRD_EVENT_TYPES table has been commented out in case the business later changes their requirements.
        // EventType eventType = optEventType.get();
        // if (eventType.getDueDays() != null) {
        //     // Override the provided response due date when the field should be system generated.
        //     eventsDto.setResponseDue(eventsDto.getDateTime().plusDays(eventType.getDueDays()));
        // } else if (
        if (
            eventsDto.getResponseDue() != null &&
            eventsDto.getResponseDue().isBefore(eventsDto.getDateTime())
        ) {
            throw new ValidationException("Response due date cannot be before the provided event date.");
        }

        if (eventsDto.getEvent().equals("FRMR") || eventsDto.getEvent().equals("PAMH")) {
            throw new ValidationException(
                "Unable to create event. The " + eventsDto.getEvent() + " " +
                "event type is reserved for system generated events."
            );
        }

        Application currentApp = app.get();
        newEvent = repo.save(getEventEntity(eventsDto, app.get()));
        EventsDto newEventDto = getEventsDto(newEvent);

        if ((newEvent.getEventTypeCode().equals("TDOR") && currentApp.getTypeCode().equals("606"))) {
            newEventDto.addMessagesItem("Reminder: You just entered a TDOR event for this 606. Check the geocodes.");
        } else if ((newEvent.getEventTypeCode().equals("REIN") && currentApp.getTypeCode().equals("606")) || (newEvent.getEventTypeCode().equals("REIN") && currentApp.getTypeCode().equals("600"))) {
            if (repo.callReinstate(app.get().getId().longValue()) > 0) {
                newEventDto.addMessagesItem("Reminder: You just reinstated this " + currentApp.getTypeCode() + ". Valid geocode end dates have been removed.");
            } else {
                throw new DataConflictException("Error reinstating. There are no water rights attached to application " + currentApp.getId());
            }
        } else if (newEvent.getEventTypeCode().equals("ISSU")) {
            repo.postInsertQueryUpdateDateAndStatus(java.sql.Date.valueOf(newEvent.getEventDate().toLocalDate()), applicationId);
            String message = repo.postInsertUpdateStatusToActive(applicationId);
            newEventDto.addMessagesItem(message.equals("UPDATED") ? "Water Right Status Updated." : message);

        } else if (newEvent.getEventTypeCode().equals("AME2") && currentApp.getTypeCode().equals("600")) {
            Integer rowUpdated = repo.postInsertUpdatePriorityDate(java.sql.Date.valueOf(newEventDto.getDateTime().toLocalDate()), applicationId);

            newEventDto.addMessagesItem(rowUpdated > 0 ?
                    "Water Right Priority Date Updated" :
                    "Water Right Priority Date Not Updated");
        } else if (Arrays.asList("EXRD", "RERD", "MODR").contains(newEvent.getEventTypeCode())) {
            BigDecimal filingFee = payService.computeFilingFee(currentApp);
            currentApp.setFilingFee(filingFee);
            appRepo.save(currentApp);
        }

        return newEventDto;
    }


    /**
     * @param applicationId
     * @param eventId
     * @param eventsDto
     * @return
     */
    @Override
    @Transactional
    public Optional<EventsDto> changeEvent(String applicationId, Long eventId, EventsDto eventsDto) {
        LOGGER.info("Changing an event");

        Optional<Application> foundApp = appRepo.findById(new BigDecimal(applicationId));
        Optional<Event> foundEvent = repo.findById(new BigDecimal(eventId));

        if (!foundApp.isPresent()) {
            throw new NotFoundException("The Application with id " + applicationId + " was not found");
        }

        Application app = foundApp.get();

        Event newEvent = null;
        if (!foundEvent.isPresent()) {
            throw new NotFoundException("The Event Type with event id " + eventId + " was not found");
        }

        Event oldEvent = foundEvent.get();

        if (!oldEvent.getApplication().getId().toString().equals(applicationId)) {
            throw new NotFoundException(
                    "The Event with eventId " + eventId + " doesn't belong to applicationId " + applicationId);
        }

        if (!validateDateTime(eventsDto.getDateTime())) {
            throw new ValidationException("Please enter an event date between January 1, 1850 and December 31, 2100.");
        }

        if (
            !app.getTypeCode().endsWith("P") &&
            oldEvent.getEventTypeCode().equals("PAMH") &&
            !oldEvent.getEventDate().equals(eventsDto.getDateTime())
        ) {
            throw new ValidationException(
                "Cannot change the date/time of a PRE-APPLICATION MEETING HELD (PAMH) event " +
                "after Application has been converted to Non-Pre type."
            );
        }

        if (
            !oldEvent.getEventTypeCode().equals(eventsDto.getEvent()) &&
            (
                oldEvent.getEventTypeCode().equals("FRMR") ||
                oldEvent.getEventTypeCode().equals("PAMH")
            )
        ) {
            throw new ValidationException(
                "Cannot change the event type of a FORM RECEIVED (FRMR) " +
                "or PRE-APPLICATION MEETING HELD (PAMH) event."
            );
        }

        Optional<EventType> optEventType = eventTypeRepo.findById(eventsDto.getEvent());
        if (!optEventType.isPresent()) {
            throw new NotFoundException("Provided event has an unrecognized type code.");
        }

        EventType eventType = optEventType.get();
        if (eventType.getDueDays() != null && !oldEvent.getEventDate().equals(eventsDto.getDateTime())) {
            // Recalculate the response due date.
            eventsDto.setResponseDue(eventsDto.getDateTime().plusDays(eventType.getDueDays()));
        }

        newEvent = repo.save(getEventEntity(eventsDto, oldEvent));
        EventsDto newEventsDto = getEventsDto(newEvent);
        if (newEvent.getEventTypeCode().equals("ISSU")) {
            String rtnVal = repo.callDoesApplWrHaveGeocodeYn(applicationId);
            if (rtnVal.equals("N")) {
                eventsDto.addMessagesItem("Reminder: You are issuing a water right that does not have a geocode!");
            }
            BigDecimal appFilingFee = app.getType().getFilingFee();
            String appFeeStatus = app.getFeeStatus();

            Integer l_frm = repo.postChangeQueryCountFormReceived(applicationId);

            if (l_frm > 0 && (appFilingFee.intValue() > 0 && !appFeeStatus.equals("FULL"))) {
                newEventsDto.addMessagesItem("Filing Fee not paid in full " + LocalDateTime.now());
            }
        }

        return Optional.ofNullable(newEventsDto);
    }

    /**
     * @param applicationId
     * @param eventId
     */
    @Override
    public void deleteEvent(String applicationId, Long eventId) {
        LOGGER.info("Deleting an event");

        Optional<Event> result = repo.findByAppIdAndEventId(new BigDecimal(applicationId), new BigDecimal(eventId));
        if (!result.isPresent()){
            throw new NotFoundException("The Event with application id: " + applicationId + " and event id: "+eventId.toString()+" is not found");
        }

        if (!result.get().getEventTypeCode().equals("FRMR") && !result.get().getEventTypeCode().equals("PAMH"))
            repo.deleteById(result.get().getEventId());
        else {
            throw new ValidationException("Cannot delete a FORM RECEIVED (FRMR) or PRE-APPLICATION MEETING HELD (PAMH) event.");
        }
        if(Arrays.asList("EXRD", "RERD", "MODR").contains(result.get().getEventTypeCode())) {
            Optional<Application> app = appRepo.findApplicationsWithType(new BigDecimal(applicationId));
            Application currentApp = app.get();
            BigDecimal filingFee = payService.computeFilingFee(currentApp);
            currentApp.setFilingFee(filingFee);
            appRepo.save(currentApp);
        }
    }

    public void deleteCaseRegisterEvent(BigDecimal caseId, BigDecimal eventId) {

        LOGGER.info("Delete Event from Case Register");

        if (masterStaffIndexesRepository.hasRoles(Arrays.asList(Constants.CASES_AND_OBJECTIONS_ADMIN_ROLE)) < 1)
            throw new ValidationException("Administrator role required to delete Events from Register.");
        repo.deleteEventByEventIdAndCourtCaseId(eventId, caseId);

    }


    /**
     * @param oldDto
     * @return
     */
    @Override
    public EventsDto toUpperCase(EventsDto oldDto) {
        EventsDto newDto = new EventsDto();
        newDto.setEvent((oldDto.getEvent() != null) ? oldDto.getEvent().toUpperCase() : null);
        newDto.setComments((oldDto.getComments() != null) ? oldDto.getComments().toUpperCase() : null);
        newDto.setDateTime(oldDto.getDateTime());
        newDto.setResponseDue(oldDto.getResponseDue());
        newDto.setCreateBy((oldDto.getCreateBy() != null) ? oldDto.getCreateBy().toUpperCase() : null);
        newDto.setModifiedBy((oldDto.getModifiedBy() != null) ? oldDto.getModifiedBy().toUpperCase() : null);
        newDto.setModifiedDate(oldDto.getModifiedDate());
        newDto.setCreatedDate(oldDto.getCreatedDate());
        newDto.setResponseDue(oldDto.getResponseDue());
        newDto.setMessages(oldDto.getMessages());
        return newDto;
    }

    /**
     * This method is to get all events by appilication id and return Pagination and results[] stores the events
     *
     * @param applicationId
     * @param pageNumber
     * @param pageSize
     * @param eventsSortColumn
     * @param sortDirection
     * @return
     */
    @Override
    public EventsPageDto findEventsByApplicationId(
            String applicationId,
            Integer pageNumber,
            Integer pageSize,
            EventsSortColumn eventsSortColumn,
            SortDirection sortDirection) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(
                sortDirection.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, getEntitySortColumn(eventsSortColumn))
        );
        Page<Event> resultPage = repo.findAllByAppId(pageable, new BigDecimal(applicationId));
        EventsPageDto page = new EventsPageDto();

        page.setResults(resultPage.getContent().stream().map(sbcd -> {
            return getEventsDto(sbcd);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(eventsSortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());
        return page;
    }


    /**
     * @param DTOColumn
     * @return
     */
    private String getEntitySortColumn(EventsSortColumn DTOColumn) {
        if (DTOColumn == EventsSortColumn.DATETIME || DTOColumn == EventsSortColumn.DISPLAYDATETIME)
            return "eventDate";
        if (DTOColumn == EventsSortColumn.EVENTID)
            return "eventId";
        if (DTOColumn == EventsSortColumn.RESPONSEDUE)
            return "responseDueDate";
        if (DTOColumn == EventsSortColumn.EVENT)
            return "eventTypeCode";
        if (DTOColumn == EventsSortColumn.COMMENTS)
            return "eventComment";
        return "eventId";
    }

    /**
     * @param dto    is the new payload for new update Event
     * @param oldOne is the existing Event
     * @return
     */
    private Event getEventEntity(EventsDto dto, Event oldOne) {
        oldOne.setEventDate(dto.getDateTime());
        oldOne.setResponseDueDate(dto.getResponseDue());
        oldOne.setEventComment(dto.getComments());
        oldOne.setEventTypeCode(dto.getEvent());

        return oldOne;
    }

    /**
     * @param dto is the new payload for Event
     * @param app is the existing Application
     * @return
     */
    private Event getEventEntity(EventsDto dto, Application app) {
        Event model = new Event();
        model.setApplication(app);
        model.setEventDate(dto.getDateTime());
        model.setResponseDueDate(dto.getResponseDue());
        model.setEventComment(dto.getComments());
        model.setEventTypeCode(dto.getEvent());
        EventType eventType = new EventType();
        eventType.setCode(dto.getEvent());
        model.setEventType(eventType);
        model.setEventId((dto.getEventId() != null) ? new BigDecimal(dto.getEventId()) : null);
        return model;
    }

    private Boolean validateDateTime(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.parse("1850-01-01T00:00:00")) && dateTime.isBefore(LocalDateTime.parse("2100-12-31T00:00:00"));
    }
}
