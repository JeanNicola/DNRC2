package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.ZipCodesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.ZipCodeDto;
import gov.mt.wris.dtos.ZipCodePageDto;
import gov.mt.wris.dtos.ZipCodeSortColumn;
import gov.mt.wris.services.ZipCodeService;

@Controller
public class ZipCodesController implements ZipCodesApiDelegate{
    private static Logger LOGGER = LoggerFactory.getLogger(ZipCodesController.class);

    @Autowired
    private ZipCodeService zipService;

    @Override
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.ZIP_CODE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CITY_TABLE)
    })
    public ResponseEntity<ZipCodePageDto> getZipCodes(Integer pageNumber, Integer pageSize, ZipCodeSortColumn sortColumn, SortDirection sortDirection, String zipCode, String cityName, String stateCode) {
        LOGGER.info("Get a list of Zip Codes");
        ZipCodePageDto zipPageDto = zipService.getZipCodes(pageNumber, pageSize, sortColumn, sortDirection, zipCode, cityName, stateCode);
        return ResponseEntity.ok(zipPageDto);
    }

    @Override
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.ZIP_CODE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CITY_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.ZIP_CODE_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.CITY_TABLE)
    })
    public ResponseEntity<ZipCodeDto> createZipCode(ZipCodeDto zipCodeDto) {
        LOGGER.info("Creating a new Zip Code, City and State combination");

        // convert everything to upper case
        zipCodeDto = zipService.toUpperCase(zipCodeDto);

        ZipCodeDto newZip = zipService.createZipCode(zipCodeDto);
        return new ResponseEntity<ZipCodeDto>(newZip, null, HttpStatus.CREATED);
    }

    @Override
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.ZIP_CODE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.ZIP_CODE_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.CITY_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CITY_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE)
    })
    public ResponseEntity<Void> deleteZipCode(Long zipCodeId) {
        LOGGER.info("Deleting a Zip Code, City and State combination");
        zipService.deleteZipCode(zipCodeId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @Override
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.ZIP_CODE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CITY_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.ZIP_CODE_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.CITY_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.CITY_TABLE)
    })
    public ResponseEntity<ZipCodeDto> changeZipCode(Long zipCodeId, ZipCodeDto zipCodeDto) {
        LOGGER.info("Changing a Zip Code, City and State combination");

        // convert everything to upper case
        zipCodeDto = zipService.toUpperCase(zipCodeDto);

        return ResponseEntity.ok(zipService.changeZipCode(zipCodeId, zipCodeDto));
    }
}
