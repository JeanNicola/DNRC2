package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.ObjectionsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.ObjectionCreationDto;
import gov.mt.wris.dtos.ObjectionsSearchResultDto;
import gov.mt.wris.dtos.ObjectionsSearchResultPageDto;
import gov.mt.wris.dtos.SearchObjectionsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.services.ObjectionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

@Controller
public class ObjectionsController implements ObjectionsApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(ObjectionsController.class);

    @Autowired
    private ObjectionsService objectionsService;

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.OBJECTIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_TYPE_TABLE)
    })
    public ResponseEntity<ObjectionsSearchResultPageDto> searchObjections(Integer pageNumber,
                                                                          Integer pageSize,
                                                                          SearchObjectionsSortColumn sortColumn,
                                                                          SortDirection sortDirection,
                                                                          String objectionId,
                                                                          String objectionType,
                                                                          LocalDate filedDate,
                                                                          String objectionLate,
                                                                          String objectionStatus,
                                                                          String basin) {

        LOGGER.info("Search Objections and Counter Objections");
        ObjectionsSearchResultPageDto dto = objectionsService.searchObjections(pageNumber, pageSize, sortColumn, sortDirection, objectionId, objectionType, filedDate, objectionLate, objectionStatus, basin);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.OBJECTIONS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.OBJECTIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OBJECTORS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.OBJECTORS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.ELEMENT_OBJECTION_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.ELEMENT_OBJECTION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<ObjectionsSearchResultDto> createObjection(ObjectionCreationDto creationDto) {

        LOGGER.info("Create Objection or Counter Objection");
        ObjectionsSearchResultDto dto = objectionsService.createObjection(creationDto);
        return new ResponseEntity<ObjectionsSearchResultDto>(dto, null, HttpStatus.CREATED);

    }



}
