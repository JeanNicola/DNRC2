package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.WaterRightTypesApiDelegate;
import gov.mt.wris.constants.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.AllWaterRightStatusesDto;
import gov.mt.wris.services.WaterRightStatusService;
import gov.mt.wris.services.WaterRightTypeService;


@Controller
public class WaterRightTypesController implements WaterRightTypesApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(WaterRightTypesController.class);

    @Autowired
    private WaterRightStatusService statusService;

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.STATUS_TYPE_XREF_TABLE)
    })
    public ResponseEntity<AllWaterRightStatusesDto> getAllWaterRightStatuses(String waterRightTypeCode) {
        LOGGER.info("Getting all Water Right Statuses");

        AllWaterRightStatusesDto all = statusService.getWaterRightStatuses(waterRightTypeCode);

        return ResponseEntity.ok(all);
    }

}
