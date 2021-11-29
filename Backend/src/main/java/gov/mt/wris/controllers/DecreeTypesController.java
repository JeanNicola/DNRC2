package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.DecreeTypesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllDecreeTypesDto;
import gov.mt.wris.services.DecreeTypeService;

@Controller
public class DecreeTypesController implements DecreeTypesApiDelegate{
    private static Logger LOGGER = LoggerFactory.getLogger(DecreeTypesController.class);

    @Autowired
    private DecreeTypeService decreeService;

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.DECREE_TYPE_TABLE)
    )
    public ResponseEntity<AllDecreeTypesDto> getAllDecreeTypes() {
        LOGGER.info("Get all the Decree Types");
        return ResponseEntity.ok(decreeService.getAllDecreeTypes());
    }
}