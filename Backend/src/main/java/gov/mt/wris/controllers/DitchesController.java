package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.DitchesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.DitchCreationDto;
import gov.mt.wris.dtos.DitchDto;
import gov.mt.wris.dtos.DitchPageDto;
import gov.mt.wris.dtos.DitchSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.services.DitchService;

@Controller
public class DitchesController implements DitchesApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(DitchesController.class);

    @Autowired
    private DitchService ditchService;

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.DITCH_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.COUNTIES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.LEGAL_LAND_DESCRIPTION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.TRS_LOCATION_TABLE)
    })
    public ResponseEntity<DitchPageDto> searchDitches(Integer pageNumber,
        Integer pageSize,
        DitchSortColumn sortColumn,
        SortDirection sortDirection,
        String ditchName
    ) {
        LOGGER.info("Searching for Ditches");

        DitchPageDto dto = ditchService.searchDitches(pageNumber, pageSize, sortColumn, sortDirection, ditchName);

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<DitchDto> createDitch(DitchCreationDto creationDto) {
        LOGGER.info("Creating a Ditch");

        DitchDto dto = ditchService.createDitch(creationDto);

        return new ResponseEntity<DitchDto>(dto, null, HttpStatus.CREATED);
    }
}
