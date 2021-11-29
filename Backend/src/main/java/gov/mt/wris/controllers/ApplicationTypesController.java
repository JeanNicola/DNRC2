package gov.mt.wris.controllers;

import gov.mt.wris.dtos.AllEventCodeDescDto;
import gov.mt.wris.services.EventTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.ApplicationTypesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllApplicationTypesDto;
import gov.mt.wris.services.ApplicationTypeService;

@Controller
public class ApplicationTypesController implements ApplicationTypesApiDelegate{
    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationTypesController.class);

    @Autowired
    private ApplicationTypeService appService;

    @Autowired
    private EventTypeService eventTypeService;

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TYPE_TABLE)
    )
    public ResponseEntity<AllApplicationTypesDto> getAllApplicationTypes() {
        LOGGER.info("Get all the Application Types");
        return ResponseEntity.ok(appService.getAllApplicationTypes());
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TYPE_TABLE)
    )
    public ResponseEntity<AllEventCodeDescDto> getEventTypesByTypeCode(String typeCode) {

        return ResponseEntity.ok(eventTypeService.getEventTypeCodeByApplicationTypeCode(typeCode));

    }
}