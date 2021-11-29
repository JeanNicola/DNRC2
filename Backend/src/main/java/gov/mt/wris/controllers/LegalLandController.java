package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.LegalLandApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.services.LegalLandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class LegalLandController implements LegalLandApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(OfficesController.class);

    @Autowired
    LegalLandService legalLandService;
    
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.COUNTIES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.LEGAL_LAND_DESCRIPTION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.TRS_LOCATION_TABLE)
    })
    public ResponseEntity<Long> getLegalLandDescriptionId(String description320, String description160, String description80, String description40, Long governmentLot, Long township, String townshipDirection, Long range, String rangeDirection, Long section, Long countyId) {
        LOGGER.info("Getting Legal Land ID");
        return ResponseEntity.ok(legalLandService.getLegalLandDescriptionId(description320, description160, description80, description40, governmentLot, township, townshipDirection, range, rangeDirection, section, countyId));
    }
}
