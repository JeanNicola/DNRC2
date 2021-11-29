package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.MeansOfDiversionsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.services.MeansOfDiversionService;

@Controller
public class MeansOfDiversionController implements MeansOfDiversionsApiDelegate {
    public static Logger LOGGER = LoggerFactory.getLogger(MeansOfDiversionController.class);

    @Autowired
    private MeansOfDiversionService meansService;

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MEANS_OF_DIVERSION_TABLE)
    })
    public ResponseEntity<AllReferencesDto> getMeansOfDiversions() {
        LOGGER.info("Getting all the Means of Diversions");

        AllReferencesDto dto = meansService.getMeansOfDiversions();

        return ResponseEntity.ok(dto);
    }
    
}
