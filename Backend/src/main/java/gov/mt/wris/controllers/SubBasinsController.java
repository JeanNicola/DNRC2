package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.SubBasinsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllSubBasinsDto;
import gov.mt.wris.services.BasinService;

@Controller
public class SubBasinsController implements SubBasinsApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(SubBasinsController.class);

    @Autowired
    private BasinService basinService;

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.BASIN_COMPACT_TABLE)
    )
    public ResponseEntity<AllSubBasinsDto> getSubBasins() {
        LOGGER.info("Get a list of Sub Basins");

        AllSubBasinsDto dto = basinService.getSubBasins();

        return ResponseEntity.ok(dto);
    }
    
}
