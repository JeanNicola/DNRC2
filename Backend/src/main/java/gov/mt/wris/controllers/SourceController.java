package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.SourcesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SourceCreationDto;
import gov.mt.wris.dtos.SourceDto;
import gov.mt.wris.dtos.SourcePageDto;
import gov.mt.wris.services.SourceService;

@Controller
public class SourceController implements SourcesApiDelegate {
    public static Logger LOGGER = LoggerFactory.getLogger(SourceController.class);

    @Autowired
    private SourceService sourceService;

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.SOURCE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.SOURCE_NAME_TABLE)
    })
    public ResponseEntity<SourcePageDto> searchSources(Integer pageNumber,
        Integer pageSize,
        SortDirection sortDirection,
        String sourceName
    ) {
        LOGGER.info("Searching for Sources");

        SourcePageDto dto = sourceService.searchSources(pageNumber, pageSize, sortDirection, sourceName);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.INSERT, table = Constants.SOURCE_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.SOURCE_NAME_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.ALSO_KNOWN_TABLE)
    })
    public ResponseEntity<SourceDto> createSource(SourceCreationDto creationDto) {
        LOGGER.info("Creating a Source");

        SourceDto dto = sourceService.createSource(creationDto);

        return new ResponseEntity<SourceDto>(dto, null, HttpStatus.CREATED);
    }
}
