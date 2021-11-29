package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.BasinsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllBasinsDto;
import gov.mt.wris.services.BasinService;

@Controller
public class BasinsController implements BasinsApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(BasinsController.class);

    @Autowired
    private BasinService basinService;

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.BASIN_COMPACT_TABLE)
    )
    public ResponseEntity<AllBasinsDto> getBasins() {
        LOGGER.info("Get a list of Basins");

        AllBasinsDto allBasins = basinService.getBasins();

        return ResponseEntity.ok(allBasins);
    }
}
