package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.OfficesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllOfficesDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.services.OfficeService;

@Controller
public class OfficesController implements OfficesApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(OfficesController.class);

    @Autowired
    private OfficeService officeService;

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE)
    })
    public ResponseEntity<AllOfficesDto> getAllOffices() {
        LOGGER.info("Getting all the offices");

        AllOfficesDto dto = officeService.getAllOffices();

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE)
    })
    public ResponseEntity<AllOfficesDto> getAllRegionalOffices() {
        LOGGER.info("Getting all the Regional offices");

        AllOfficesDto dto = officeService.getAllRegionalOffices();

        return ResponseEntity.ok(dto);
    }

}
