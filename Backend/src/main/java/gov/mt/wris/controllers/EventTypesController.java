package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.EventTypesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.EventTypePageDto;
import gov.mt.wris.dtos.AllApplicationTypesDto;
import gov.mt.wris.dtos.AllCaseTypesDto;
import gov.mt.wris.dtos.AllDecreeTypesDto;
import gov.mt.wris.dtos.AllEventCodeDescDto;
import gov.mt.wris.dtos.EventTypeDto;
import gov.mt.wris.dtos.EventTypeSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.TypeXrefDto;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.services.ApplicationTypeXrefService;
import gov.mt.wris.services.CaseTypeXrefService;
import gov.mt.wris.services.DecreeTypeXrefService;
import gov.mt.wris.services.EventTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;


@Controller
public class EventTypesController implements EventTypesApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(EventTypesController.class);

    @Autowired
    private EventTypeService eventService;

    @Autowired
    private CaseTypeXrefService caseXrefService;
    @Autowired
    private ApplicationTypeXrefService applicationXrefService;
    @Autowired
    private DecreeTypeXrefService decreeXrefService;

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE)
    )
    public ResponseEntity<EventTypePageDto> getEventTypes(Integer pagenumber, Integer pagesize, EventTypeSortColumn sortcolumn, SortDirection sortdirection, String code, String description, String responseDueDate) {
        LOGGER.info("Getting a Page of Event Types");
        EventTypePageDto eventPageDto = eventService.getEventTypes(pagenumber, pagesize, sortcolumn, sortdirection, code, description, responseDueDate);
        return ResponseEntity.ok(eventPageDto);
    }

    // @Override
    // @PermissionsNeeded({
    //     @Permission(verb = Constants.SELECT, table=Constants.EVENT_TYPE_TABLE),
    //     @Permission(verb = Constants.SELECT, table=Constants.APPLICATION_TYPE_XREF_TABLE),
    //     @Permission(verb = Constants.SELECT, table=Constants.CASE_TYPE_XREF_TABLE),
    //     @Permission(verb = Constants.SELECT, table=Constants.DECREE_TYPE_XREF_TABLE)
    // })
    // public ResponseEntity<EventTypeScreenDto> getEventTypeScreen(String code) {
    //     code = code.toUpperCase();
    //     EventTypeScreenDto screen = eventService.getEventTypeScreen(code);
    //     return ResponseEntity.ok(screen);
    // }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.EVENT_TYPE_TABLE)
    })
    public ResponseEntity<EventTypeDto> createEventType(EventTypeDto eventTypeDto) throws HttpMessageNotReadableException {
        LOGGER.info("Creating a new Event Type.");

        // convert everything to upper case
        eventTypeDto = eventService.toUpperCase(eventTypeDto);

        EventTypeDto savedEvent = eventService.createEventType(eventTypeDto);
        return new ResponseEntity<EventTypeDto>(savedEvent, null, HttpStatus.CREATED);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.DELETE, table = Constants.EVENT_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TYPE_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_TYPE_XREF_TABLE)
    })
    public ResponseEntity<Void> deleteEventType(String code) {
        LOGGER.info("Deleting an Event Type");
        //convert everything to upper case
        code = code.toUpperCase();
        eventService.deleteEventType(code);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.UPDATE, table = Constants.EVENT_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE)
    })
    public ResponseEntity<EventTypeDto> changeEventType(String code, EventTypeDto eventTypeDto) {
        // convert everything to upper case
        eventTypeDto = eventService.toUpperCase(eventTypeDto);
        code = code.toUpperCase();

        // Check that we're not changing the code
        if (!code.equals(eventTypeDto.getCode()))
            throw new DataConflictException("Changing the Event Type Code isn't allowed. Delete the Event Type and create a new one");

        EventTypeDto changedEvent = eventService.replaceEventType(eventTypeDto, code);
        if (changedEvent.getCode().equals(eventTypeDto.getCode())) {
            return ResponseEntity.ok(changedEvent);
        } else {
            return new ResponseEntity<EventTypeDto>(changedEvent, null, HttpStatus.MOVED_PERMANENTLY);
        }
    }

    // Case Type Xrefs
    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE)
    })
    public ResponseEntity<AllCaseTypesDto> getEventCaseTypes(String eventCode) {
        LOGGER.info("Getting the Case Types for the event type: " + eventCode);

        eventCode = eventCode.toUpperCase();

        AllCaseTypesDto caseTypes = eventService.getEventCaseTypes(eventCode);

        return ResponseEntity.ok(caseTypes);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_XREF_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.CASE_TYPE_XREF_TABLE)
    })
    public ResponseEntity<TypeXrefDto> createCaseTypeXref(String code, TypeXrefDto typeXrefDto) {
        LOGGER.info("Adding a Case Type " + typeXrefDto.getCode() + " to an Event Type " + code);

        code = code.toUpperCase();
        caseXrefService.toUpperCase(typeXrefDto);

        TypeXrefDto typeXref = caseXrefService.addCaseType(code, typeXrefDto);
        return new ResponseEntity<TypeXrefDto>(typeXref, null, HttpStatus.CREATED);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.DELETE, table = Constants.CASE_TYPE_XREF_TABLE)
    )
    public ResponseEntity<Void> deleteCaseTypeXref(String code, String caseCode) {
        LOGGER.info("Deleting an Case Type from an Event");
        //convert everything to upper case
        code = code.toUpperCase();
        caseCode = caseCode.toUpperCase();
        caseXrefService.removeCaseType(code, caseCode);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }


    // Application Type Xrefs
    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TYPE_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE)
    })
    public ResponseEntity<AllApplicationTypesDto> getEventApplicationTypes(String eventCode) {
        LOGGER.info("Getting the Application Types for the event type: " + eventCode);

        eventCode = eventCode.toUpperCase();

        AllApplicationTypesDto appTypes = eventService.getEventApplicationTypes(eventCode);

        return ResponseEntity.ok(appTypes);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TYPE_XREF_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.APPLICATION_TYPE_XREF_TABLE)
    })
    public ResponseEntity<TypeXrefDto> createApplicationTypeXref(String code, TypeXrefDto typeXrefDto) {
        LOGGER.info("Adding an Application Type " + typeXrefDto.getCode() + " to an Event Type " + code);

        code = code.toUpperCase();
        applicationXrefService.toUpperCase(typeXrefDto);

        TypeXrefDto typeXref = applicationXrefService.addApplicationType(code, typeXrefDto);
        return new ResponseEntity<TypeXrefDto>(typeXref, null, HttpStatus.CREATED);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.DELETE, table = Constants.APPLICATION_TYPE_XREF_TABLE)
    )
    public ResponseEntity<Void> deleteApplicationTypeXref(String code, String applicationCode) {
        LOGGER.info("Deleting an Application Type from an Event Type");
        //convert everything to upper case
        code = code.toUpperCase();
        applicationCode = applicationCode.toUpperCase();

        applicationXrefService.removeApplicationType(code, applicationCode);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    // Decree Type Xrefs
    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_TYPE_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE)
    })
    public ResponseEntity<AllDecreeTypesDto> getEventDecreeTypes(String eventCode) {
        LOGGER.info("Getting the Decree Types for the event type: " + eventCode);

        eventCode = eventCode.toUpperCase();

        AllDecreeTypesDto decreeTypes = eventService.getEventDecreeTypes(eventCode);

        return ResponseEntity.ok(decreeTypes);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_TYPE_XREF_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.DECREE_TYPE_XREF_TABLE)
    })
    public ResponseEntity<TypeXrefDto> createDecreeTypeXref(String code, TypeXrefDto typeXrefDto) {
        LOGGER.info("Adding a Decree Type " + typeXrefDto.getCode() + " to an Event Type " + code);

        code = code.toUpperCase();
        decreeXrefService.toUpperCase(typeXrefDto);

        TypeXrefDto typeXref = decreeXrefService.addDecreeType(code, typeXrefDto);
        return new ResponseEntity<TypeXrefDto>(typeXref, null, HttpStatus.CREATED);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.DELETE, table = Constants.DECREE_TYPE_XREF_TABLE)
    )
    public ResponseEntity<Void> deleteDecreeTypeXref(String code, String decreeCode) {
        LOGGER.info("Deleting a Decree Type from an Event Type");
        //convert everything to upper case
        code = code.toUpperCase();
        decreeCode = decreeCode.toUpperCase();

        decreeXrefService.removeDecreeType(code, decreeCode);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE)
    )
    public ResponseEntity<AllEventCodeDescDto> getEventTypesAll() {
        return ResponseEntity.ok(eventService.getAllEventCodeDesc());
    }
}