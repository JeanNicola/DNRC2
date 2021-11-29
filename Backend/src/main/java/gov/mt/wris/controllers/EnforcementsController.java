package gov.mt.wris.controllers;

import gov.mt.wris.dtos.EnforcementPodPageDto;
import gov.mt.wris.dtos.EnforcementPodsSortColumn;
import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.EnforcementsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllEnforcementsDto;
import gov.mt.wris.dtos.EnforcementDto;
import gov.mt.wris.dtos.EnforcementsSearchPageDto;
import gov.mt.wris.dtos.EnforcementsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.services.EnforcementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class EnforcementsController implements EnforcementsApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(EnforcementsApiDelegate.class);

    @Autowired
    private EnforcementService enforcementService;

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.ENFORCEMENT_AREA_TABLE)
    )
    public ResponseEntity<AllEnforcementsDto> getAllEnforcements() {
        LOGGER.info("Finding all the Enforcement Areas");

        AllEnforcementsDto dto = enforcementService.findAll();

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.INSERT, table = Constants.ENFORCEMENT_AREA_TABLE)
    )
    public ResponseEntity<EnforcementDto> createEnforcement(EnforcementDto creationDto) {
        LOGGER.info("Creating a new Enforcement Area");

        EnforcementDto dto = enforcementService.createEnforcement(creationDto);

        return new ResponseEntity(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ENFORCEMENT_AREA_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE)
    })
    public ResponseEntity<EnforcementsSearchPageDto> searchEnforcements(Integer pageNumber,
                                                                        Integer pageSize,
                                                                        EnforcementsSortColumn sortColumn,
                                                                        SortDirection sortDirection,
                                                                        String enforcementArea,
                                                                        String enforcementName,
                                                                        String enforcementNumber,
                                                                        String basin,
                                                                        String waterNumber) {

        LOGGER.info("Search Enforcement Area");
        EnforcementsSearchPageDto dto = enforcementService.searchEnforcements(pageNumber, pageSize, sortColumn, sortDirection, enforcementArea, enforcementName, enforcementNumber, basin, waterNumber);;
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ENFORCEMENT_AREA_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE)
    })
    public ResponseEntity<EnforcementPodPageDto> getEnforcementPods(String areaId,
                                                                    Integer pageNumber,
                                                                    Integer pageSize,
                                                                    EnforcementPodsSortColumn sortColumn,
                                                                    SortDirection sortDirection) {

        LOGGER.info("Get Enforcement Area PODs");
        EnforcementPodPageDto dto = enforcementService.getEnforcementPods(pageNumber, pageSize, sortColumn, sortDirection, areaId);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ENFORCEMENT_AREA_TABLE)
    })
    public ResponseEntity<EnforcementDto> getEnforcement(String areaId) {

        LOGGER.info("Get Enforcement Area");
        EnforcementDto dto = enforcementService.getEnforcement(areaId);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE)
    })
    public ResponseEntity<EnforcementDto> updateEnforcementArea(String areaId, EnforcementDto updateDto) {

        LOGGER.info("Update Enforcement Area");
        EnforcementDto dto = enforcementService.updateEnforcementArea(areaId, updateDto);
        return ResponseEntity.ok(dto);

    }

}
