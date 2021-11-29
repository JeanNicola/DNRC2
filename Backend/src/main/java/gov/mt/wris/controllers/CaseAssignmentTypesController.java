package gov.mt.wris.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;

import gov.mt.wris.services.CaseAssignmentTypeService;
import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.CaseAssignmentTypesApiDelegate;
import gov.mt.wris.dtos.CaseAssignmentTypePageDto;
import gov.mt.wris.dtos.CaseAssignmentTypeDto;
import gov.mt.wris.dtos.SortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.constants.Constants;

@Controller
public class CaseAssignmentTypesController implements CaseAssignmentTypesApiDelegate{
    private static Logger LOGGER = LoggerFactory.getLogger(CaseAssignmentTypesController.class);

    @Autowired
    private CaseAssignmentTypeService caseService;

    @Override
    @PermissionsNeeded(
        @Permission(verb=Constants.SELECT, table=Constants.CASE_ASSIGNMENT_TYPES_TABLE)
    )
    public ResponseEntity<CaseAssignmentTypePageDto> getCases(Integer pagenumber, Integer pagesize, SortColumn sortcolumn, SortDirection sortdirection, String code, String assignmentType, String program) {
        LOGGER.info("Getting a Page of Case Assignment Types");
        // Convert everything to uppercase
        if(code != null) code = code.toUpperCase();
        if(assignmentType != null) assignmentType = assignmentType.toUpperCase();
        if(program != null) program = program.toUpperCase();
        CaseAssignmentTypePageDto casePageDto = caseService.getCaseAssignmentTypes(pagenumber, pagesize, sortcolumn, sortdirection, code, assignmentType, program);
        return ResponseEntity.ok(casePageDto);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.INSERT, table = Constants.CASE_ASSIGNMENT_TYPES_TABLE)
    )
    public ResponseEntity<CaseAssignmentTypeDto> createCase(@Valid CaseAssignmentTypeDto caseAssignmentTypeDto) throws HttpMessageNotReadableException {
        LOGGER.info("Creating a new Case Assignment Type.");

        // convert everything to upper case
        caseAssignmentTypeDto = caseService.toUpperCase(caseAssignmentTypeDto);

        CaseAssignmentTypeDto savedCase = caseService.createCase(caseAssignmentTypeDto);
        return new ResponseEntity<CaseAssignmentTypeDto>(savedCase, null, HttpStatus.CREATED);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.DELETE, table = Constants.CASE_ASSIGNMENT_TYPES_TABLE)
    )
    public ResponseEntity<Void> deleteCase(String code) {
        LOGGER.info("Deleting a Case Assignment Type");
        // convert everything to upper case
        code = code.toUpperCase();
        caseService.deleteCase(code);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.UPDATE, table = Constants.CASE_ASSIGNMENT_TYPES_TABLE)
    )
    public ResponseEntity<CaseAssignmentTypeDto> changeCase(String code, CaseAssignmentTypeDto caseAssignmentTypeDto) throws HttpMessageNotReadableException{
        // convert everything to upper case
        caseAssignmentTypeDto = caseService.toUpperCase(caseAssignmentTypeDto);
        code = code.toUpperCase();
        
        // Check that we're not changing the code
        if(!code.equals(caseAssignmentTypeDto.getCode())) throw new DataConflictException("Changing the Case Assignment Type Code isn't allowed. Delete the Case Assignment Type and create a new one");
        
        CaseAssignmentTypeDto changedCase = caseService.replaceCase(caseAssignmentTypeDto, code);
        if(changedCase.getCode().equals(caseAssignmentTypeDto.getCode())) {
            return ResponseEntity.ok(changedCase);
        } else {
            return new ResponseEntity<CaseAssignmentTypeDto>(changedCase, null, HttpStatus.MOVED_PERMANENTLY);
        }
    }
}