package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.StateCodesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllStateCodesDto;
import gov.mt.wris.services.StateCodeService;

@Controller
public class StateCodesController implements StateCodesApiDelegate{
    private static Logger LOGGER = LoggerFactory.getLogger(StateCodesController.class);

    @Autowired
    private StateCodeService stateService;

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.STATE_TABLE)
    )
    public ResponseEntity<AllStateCodesDto> getAllStateCodes() {
        LOGGER.info("Get all the State Codes");
        return ResponseEntity.ok(stateService.getAllStateCodes());
    }
}