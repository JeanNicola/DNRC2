package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.WaterRightVersionsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.WaterRightVersionPageDto;
import gov.mt.wris.dtos.WaterRightVersionSortColumn;
import gov.mt.wris.services.WaterRightVersionService;

@Controller
public class WaterRightVersionsController implements WaterRightVersionsApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(WaterRightVersionsController.class);

    @Autowired
    private WaterRightVersionService versionService;

    @Override
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
    })
    public ResponseEntity<WaterRightVersionPageDto> searchWaterRightVersions(Integer pageNumber,
        Integer pageSize,
        WaterRightVersionSortColumn sortColumn,
        DescSortDirection sortDirection,
        String basin,
        String waterRightNumber,
        String version
    ) {
        LOGGER.info("Searching for Water Right Versions");

        WaterRightVersionPageDto dto = versionService.searchWaterRights(pageNumber, pageSize, sortColumn, sortDirection, basin, waterRightNumber, version);

        return ResponseEntity.ok(dto);
    }
}
