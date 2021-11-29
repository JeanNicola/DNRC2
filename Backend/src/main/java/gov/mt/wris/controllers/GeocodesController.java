package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.GeocodesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.GeocodeWaterRightPageDto;
import gov.mt.wris.dtos.GeocodeWaterRightSortColumn;
import gov.mt.wris.services.WaterRightGeocodeService;

@Controller
public class GeocodesController implements GeocodesApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(GeocodesController.class);

    @Autowired
    private WaterRightGeocodeService geocodeService;
    
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_GEOCODE_XREF_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
    })
    public ResponseEntity<GeocodeWaterRightPageDto> getGeocodeWaterRights(String geocodeId,
        Integer pageNumber,
        Integer pageSize,
        GeocodeWaterRightSortColumn sortColumn,
        DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of water rights belonging to a geocode");

        GeocodeWaterRightPageDto dto = geocodeService.getGeocodeWaterRights(pageNumber, pageSize, sortColumn, sortDirection, geocodeId);

        return ResponseEntity.ok(dto);
    }
}
