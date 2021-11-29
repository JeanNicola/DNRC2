package gov.mt.wris.controllers;

import gov.mt.wris.dtos.AllEventCodeDescDto;
import gov.mt.wris.services.EventTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;

import gov.mt.wris.services.CaseTypeService;
import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.CaseTypesApiDelegate;
import gov.mt.wris.dtos.CaseTypePageDto;
import gov.mt.wris.dtos.AllCaseTypesDto;
import gov.mt.wris.dtos.CaseTypeDto;
import gov.mt.wris.dtos.CaseTypeSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.constants.Constants;

@Controller
public class CaseTypesController implements CaseTypesApiDelegate{
    private static Logger LOGGER = LoggerFactory.getLogger(CaseTypesController.class);

    @Autowired
    private CaseTypeService caseService;

    @Autowired
    private EventTypeService eventTypeService;

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_TABLE)
    )
    public ResponseEntity<AllCaseTypesDto> getAllCaseTypes() {
        LOGGER.info("Get all the Case Types");
        return ResponseEntity.ok(caseService.getAllCaseTypes());
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb=Constants.SELECT, table=Constants.CASE_TYPE_TABLE)
    )
    public ResponseEntity<CaseTypePageDto> getCaseTypes(Integer pagenumber, Integer pagesize, CaseTypeSortColumn sortcolumn, SortDirection sortdirection, String code, String description, String program) {
        LOGGER.info("Getting a Page of Case Types");
        // convert everything to uppercase
        if(code != null) code = code.toUpperCase();
        if(description != null) description = description.toUpperCase();
        if(program != null) program = program.toUpperCase();
        CaseTypePageDto casePageDto = caseService.getCaseTypes(pagenumber, pagesize, sortcolumn, sortdirection, code, description, program);
        return ResponseEntity.ok(casePageDto);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.INSERT, table = Constants.CASE_TYPE_TABLE)
    )
    public ResponseEntity<CaseTypeDto> createCaseType(CaseTypeDto caseTypeDto) throws HttpMessageNotReadableException {
        LOGGER.info("Creating a new Case Type.");

        // convert everything to upper case
        caseTypeDto = caseService.toUpperCase(caseTypeDto);

        CaseTypeDto savedCase = caseService.createCase(caseTypeDto);
        return new ResponseEntity<CaseTypeDto>(savedCase, null, HttpStatus.CREATED);
    }

    @Override
    @PermissionsNeeded({
        @Permission(verb = Constants.DELETE, table = Constants.CASE_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_XREF_TABLE)
    })
    public ResponseEntity<Void> deleteCaseType(String code) {
        LOGGER.info("Deleting a Case Type");
        // convert everything to upper case
        code = code.toUpperCase();
        caseService.deleteCase(code);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.UPDATE, table = Constants.CASE_TYPE_TABLE)
    )
    public ResponseEntity<CaseTypeDto> changeCaseType(String code, CaseTypeDto caseTypeDto) throws HttpMessageNotReadableException {
        // convert everything to upper case
        caseTypeDto = caseService.toUpperCase(caseTypeDto);
        code = code.toUpperCase();

        // Check that we're not changing the code
        if(!code.equals(caseTypeDto.getCode())) throw new DataConflictException("Changing the Case Type Code isn't allowed. Delete the Case Type and create a new one");
        
        CaseTypeDto changedCase = caseService.replaceCase(caseTypeDto, code);
        if(changedCase.getCode().equals(caseTypeDto.getCode())) {
            return ResponseEntity.ok(changedCase);
        } else {
            return new ResponseEntity<CaseTypeDto>(changedCase, null, HttpStatus.MOVED_PERMANENTLY);
        }
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE)
    })
    public ResponseEntity<AllEventCodeDescDto> getEventTypesByCaseType(String typeCode, Integer supported) {

        LOGGER.info("Get eligible Event Types for a Case or Hearing Type");
        return ResponseEntity.ok(eventTypeService.getEventTypeCodeByCaseTypeCode(typeCode, supported));

    }

}